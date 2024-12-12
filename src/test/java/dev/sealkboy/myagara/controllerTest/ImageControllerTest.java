package dev.sealkboy.myagara.controllerTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import dev.sealkboy.myagara.controller.ImageController;
import dev.sealkboy.myagara.model.Image;
import dev.sealkboy.myagara.service.ImageService;

class ImageControllerTest {

    @InjectMocks
    private ImageController imageController;

    @Mock
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadImage() {
        MultipartFile mockFile = mock(MultipartFile.class);
        doReturn(new Image()).when(imageService).uploadImage(mockFile);
        ResponseEntity<String> response = imageController.uploadImage(mockFile);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode()); 
        assertEquals("Image Uploaded", response.getBody());
        verify(imageService, times(1)).uploadImage(mockFile);
    }


    @Test
    void testGetAllImages() {
        Image mockImage1 = new Image();
        mockImage1.setId("1");
        mockImage1.setFilename("image1.jpg");

        Image mockImage2 = new Image();
        mockImage2.setId("2");
        mockImage2.setFilename("image2.jpg");

        List<Image> mockImages = Arrays.asList(mockImage1, mockImage2);
        when(imageService.getAllImages()).thenReturn(mockImages);

        ResponseEntity<List<Image>> response = imageController.getAllImages();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());
        verify(imageService, times(1)).getAllImages();
    }

    @Test
    void testGetImageById() {
        String id = "123";
        Image mockImage = new Image();
        mockImage.setId(id);
        mockImage.setFilename("image.jpg");

        when(imageService.getImageById(id)).thenReturn(mockImage);

        ResponseEntity<Image> response = imageController.getImageById(id);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(id, response.getBody().getId());
        verify(imageService, times(1)).getImageById(id);
    }

    @Test
    void testUpdateImageMetadata() {
        String id = "123";
        Image updatedImage = new Image();
        updatedImage.setFilename("updated_image.jpg");

        Image mockUpdatedImage = new Image();
        mockUpdatedImage.setId(id);
        mockUpdatedImage.setFilename("updated_image.jpg");

        when(imageService.updateImageMetadata(eq(id), any(Image.class))).thenReturn(mockUpdatedImage);

        ResponseEntity<Image> response = imageController.updateImageMetadata(id, updatedImage);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("updated_image.jpg", response.getBody().getFilename());
        verify(imageService, times(1)).updateImageMetadata(eq(id), any(Image.class));
    }

    @Test
    void testDeleteImageById() {
        String id = "123";
        doNothing().when(imageService).deleteImageById(id);

        ResponseEntity<String> response = imageController.deleteImageById(id);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Image Deleted", response.getBody());
        verify(imageService, times(1)).deleteImageById(id);
    }

    @Test
    void testDeleteAllImages() {
        doNothing().when(imageService).deleteAllImages();

        ResponseEntity<String> response = imageController.deleteAllImages();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("All Images Deleted", response.getBody());
        verify(imageService, times(1)).deleteAllImages();
    }
}
