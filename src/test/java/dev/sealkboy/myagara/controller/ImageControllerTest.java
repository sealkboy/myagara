package dev.sealkboy.myagara.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.sealkboy.myagara.model.Image;
import dev.sealkboy.myagara.service.ImageService;

class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageController imageController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();
    }

    @Test
    void testUploadImage() throws Exception {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
                "image", 
                "leaf.jpg", 
                MediaType.IMAGE_JPEG_VALUE, 
                "test image content".getBytes()
        );

        Image mockImage = new Image();
        mockImage.setId("1");
        mockImage.setFilename("leaf.jpg");
        mockImage.setLabel("Tomato___Bacterial_spot");
        mockImage.setConfidence(97.5);

        when(imageService.uploadImage(any())).thenReturn(mockImage);

        // Act & Assert
        mockMvc.perform(multipart("/api/images/upload").file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.filename").value("leaf.jpg"))
                .andExpect(jsonPath("$.label").value("Tomato___Bacterial_spot"))
                .andExpect(jsonPath("$.confidence").value(97.5));
    }

    @Test
    void testGetAllImages() throws Exception {
        // Arrange
        Image image1 = new Image();
        image1.setId("1");
        image1.setFilename("leaf1.jpg");
        image1.setLabel("Tomato___Bacterial_spot");
        image1.setConfidence(97.5);

        Image image2 = new Image();
        image2.setId("2");
        image2.setFilename("leaf2.jpg");
        image2.setLabel("Corn___Healthy");
        image2.setConfidence(98.2);

        List<Image> images = Arrays.asList(image1, image2);
        when(imageService.getAllImages()).thenReturn(images);

        // Act & Assert
        mockMvc.perform(get("/api/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
    }

    @Test
    void testGetImageById() throws Exception {
        // Arrange
        Image mockImage = new Image();
        mockImage.setId("1");
        mockImage.setFilename("leaf.jpg");
        mockImage.setLabel("Tomato___Bacterial_spot");
        mockImage.setConfidence(97.5);

        when(imageService.getImageById("1")).thenReturn(mockImage);

        // Act & Assert
        mockMvc.perform(get("/api/images/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.filename").value("leaf.jpg"));
    }

    @Test
    void testDeleteImageById() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/images/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Image Deleted"));

        verify(imageService, times(1)).deleteImageById("1");
    }
}
