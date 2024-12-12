package dev.sealkboy.myagara.controllerTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import dev.sealkboy.myagara.ml.TensorFlowClient;
import dev.sealkboy.myagara.model.Image;

@ExtendWith(MockitoExtension.class)
public class ImageControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TensorFlowClient tensorFlowClient;

    private File mockImageFile;

    @BeforeEach
    public void setUp() {
        mockImageFile = mock(File.class);
        when(mockImageFile.exists()).thenReturn(true);
    }

    @Test
    public void classifyImage_shouldReturnImageWithLabelAndConfidence_whenApiResponseIsSuccessful() {
        // Arrange
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("label", "dog");
        responseBody.put("confidence", 0.95);

        ResponseEntity<Map<String, Object>> mockResponse = new ResponseEntity<>(
            responseBody,
            HttpStatus.OK
        );

        when(restTemplate.exchange(
            eq("http://localhost:5000/classify"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(mockResponse);

        // Act
        Image result = tensorFlowClient.classifyImage(mockImageFile);

        // Assert
        assertNotNull(result);
        assertEquals("dog", result.getLabel());
        assertEquals(0.95, result.getConfidence(), 0.001);

        verify(restTemplate).exchange(
            eq("http://localhost:5000/classify"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void classifyImage_shouldThrowRuntimeException_whenApiResponseIsUnsuccessful() {
        // Arrange
        ResponseEntity<Map<String, Object>> mockResponse = new ResponseEntity<>(
            null,
            HttpStatus.INTERNAL_SERVER_ERROR
        );

        when(restTemplate.exchange(
            eq("http://localhost:5000/classify"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(mockResponse);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tensorFlowClient.classifyImage(mockImageFile);
        });

        assertEquals("Failed to classify image. Status: 500 INTERNAL_SERVER_ERROR", exception.getMessage());

        verify(restTemplate).exchange(
            eq("http://localhost:5000/classify"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void classifyImage_shouldThrowRuntimeException_whenConnectionFails() {
        // Arrange
        when(restTemplate.exchange(
            eq("http://localhost:5000/classify"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Connection error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tensorFlowClient.classifyImage(mockImageFile);
        });

        assertEquals("Error connecting to Flask API", exception.getMessage());
    }

    @Test
    public void classifyImage_shouldUseDefaultConfidence_whenConfidenceIsNotPresent() {
        // Arrange
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("label", "cat");

        ResponseEntity<Map<String, Object>> mockResponse = new ResponseEntity<>(
            responseBody,
            HttpStatus.OK
        );

        when(restTemplate.exchange(
            eq("http://localhost:5000/classify"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(mockResponse);

        // Act
        Image result = tensorFlowClient.classifyImage(mockImageFile);

        // Assert
        assertNotNull(result);
        assertEquals("cat", result.getLabel());
        assertEquals(0.0, result.getConfidence(), 0.001);
    }

    @Test
    public void classifyImage_shouldReturnUnknownLabel_whenLabelIsNotPresent() {
        // Arrange
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("confidence", 0.8);

        ResponseEntity<Map<String, Object>> mockResponse = new ResponseEntity<>(
            responseBody,
            HttpStatus.OK
        );

        when(restTemplate.exchange(
            eq("http://localhost:5000/classify"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(mockResponse);

        // Act
        Image result = tensorFlowClient.classifyImage(mockImageFile);

        // Assert
        assertNotNull(result);
        assertEquals("unknown", result.getLabel());
        assertEquals(0.8, result.getConfidence(), 0.001);
    }

    @Test
    public void classifyImage_shouldReturnImageWithDefaultValues_whenResponseBodyIsNull() {
        // Arrange
        ResponseEntity<Map<String, Object>> mockResponse = new ResponseEntity<>(
            null,
            HttpStatus.OK
        );

        when(restTemplate.exchange(
            eq("http://localhost:5000/classify"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(mockResponse);

        // Act
        Image result = tensorFlowClient.classifyImage(mockImageFile);

        // Assert
        assertNotNull(result);
        assertEquals("unknown", result.getLabel());
        assertEquals(0.0, result.getConfidence(), 0.001);
    }

    @Test
    public void classifyImage_shouldThrowRuntimeException_whenImageFileDoesNotExist() {
        // Arrange
        when(mockImageFile.exists()).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tensorFlowClient.classifyImage(mockImageFile);
        });

        assertEquals("Error connecting to Flask API", exception.getMessage());
    }

    @Test
    public void classifyImage_shouldThrowRuntimeException_whenImageFileIsNull() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tensorFlowClient.classifyImage(null);
        });

        assertEquals("Error connecting to Flask API", exception.getMessage());
    }
}