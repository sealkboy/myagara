package dev.sealkboy.myagara.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import dev.sealkboy.myagara.ml.TensorFlowClient;
import dev.sealkboy.myagara.model.Image;
import dev.sealkboy.myagara.repository.ImageRepository;
import dev.sealkboy.myagara.service.ImageService;

class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private TensorFlowClient tensorFlowClient;

    @Mock
    private ImageRepository imageRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadImageSuccess() throws Exception {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
                "image", "leaf.jpg", "image/jpeg", "test content".getBytes()
        );

        Image mockImage = new Image();
        mockImage.setLabel("Tomato___Healthy");
        mockImage.setConfidence(99.0);

        when(tensorFlowClient.classifyImage(any(File.class))).thenReturn(mockImage);
        when(imageRepository.save(any(Image.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Image uploadedImage = imageService.uploadImage(mockFile);

        // Assert
        assertNotNull(uploadedImage);
        assertEquals("Tomato___Healthy", uploadedImage.getLabel());
        assertEquals(99.0, uploadedImage.getConfidence());
        verify(imageRepository, times(1)).save(any(Image.class));
    }

    @Test
    void testUploadImageWithNullFile() {
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> imageService.uploadImage(null));
        assertEquals("Error uploading and processing the image", exception.getMessage());
    }

    @Test
    void testSaveImageLocallyFailure() throws IllegalStateException, IOException {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
                "image", "leaf.jpg", "image/jpeg", "test content".getBytes()
        );

        doThrow(new IOException("Directory creation failed")).when(mockFile).transferTo(any(File.class));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> imageService.uploadImage(mockFile));
        assertTrue(exception.getMessage().contains("Error uploading and processing the image"));
    }

    @Test
    void testTensorFlowClientThrowsException() {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
                "image", "leaf.jpg", "image/jpeg", "test content".getBytes()
        );

        when(tensorFlowClient.classifyImage(any(File.class)))
                .thenThrow(new RuntimeException("Flask server is unavailable"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> imageService.uploadImage(mockFile));
        assertTrue(exception.getMessage().contains("Flask server is unavailable"));
    }

    @Test
    void testGetImageByIdSuccess() {
        // Arrange
        Image mockImage = new Image();
        mockImage.setId("1");
        mockImage.setLabel("Corn___Healthy");
        mockImage.setConfidence(98.0);

        when(imageRepository.findById("1")).thenReturn(Optional.of(mockImage));

        // Act
        Image result = imageService.getImageById("1");

        // Assert
        assertNotNull(result);
        assertEquals("Corn___Healthy", result.getLabel());
        verify(imageRepository, times(1)).findById("1");
    }

    @Test
    void testGetImageByIdNotFound() {
        // Arrange
        when(imageRepository.findById("1")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> imageService.getImageById("1"));
        assertEquals("Image not found with ID: 1", exception.getMessage());
    }

    @Test
    void testUpdateImageMetadataSuccess() {
        // Arrange
        Image existingImage = new Image();
        existingImage.setId("1");
        existingImage.setLabel("Corn___Healthy");
        existingImage.setConfidence(90.0);

        Image updatedImage = new Image();
        updatedImage.setLabel("Corn___Blight");
        updatedImage.setConfidence(92.0);

        when(imageRepository.findById("1")).thenReturn(Optional.of(existingImage));
        when(imageRepository.save(any(Image.class))).thenReturn(existingImage);

        // Act
        Image result = imageService.updateImageMetadata("1", updatedImage);

        // Assert
        assertEquals("Corn___Blight", result.getLabel());
        assertEquals(92.0, result.getConfidence());
        verify(imageRepository, times(1)).save(existingImage);
    }

    @Test
    void testUpdateImageMetadataNotFound() {
        // Arrange
        Image updatedImage = new Image();
        updatedImage.setLabel("Corn___Blight");

        when(imageRepository.findById("1")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            imageService.updateImageMetadata("1", updatedImage);
        });

        assertEquals("Image not found with ID: 1", exception.getMessage());
    }

    @Test
    void testDeleteImageByIdSuccess() {
        // Arrange
        when(imageRepository.existsById("1")).thenReturn(true);

        // Act
        imageService.deleteImageById("1");

        // Assert
        verify(imageRepository, times(1)).deleteById("1");
    }

    @Test
    void testDeleteImageByIdNotFound() {
        // Arrange
        when(imageRepository.existsById("1")).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> imageService.deleteImageById("1"));
        assertEquals("Image not found with ID: 1", exception.getMessage());
    }

    @Test
    void testDeleteAllImages() {
        // Act
        imageService.deleteAllImages();

        // Assert
        verify(imageRepository, times(1)).deleteAll();
    }
}
