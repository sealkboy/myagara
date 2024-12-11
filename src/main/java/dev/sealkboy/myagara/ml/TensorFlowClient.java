package dev.sealkboy.myagara.ml;

import java.io.File;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import dev.sealkboy.myagara.model.Image;

@Component
public class TensorFlowClient {

    // Local Flask API URL
    private static final String FLASK_URL = "http://localhost:5000/classify";

    /**
     * Sends the image to the Flask API for classification.
     * @param imageFile File object representing the image to classify.
     * @return Image object with classification results.
     */
    public Image classifyImage(File imageFile) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // Prepare body with the image file
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new FileSystemResource(imageFile));

            // Create the HTTP request
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Send POST request to Flask API
            ResponseEntity<Map> response = restTemplate.postForEntity(FLASK_URL, requestEntity, Map.class);

            // Handle response
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                // Extract data from the response and map it to the Image object
                Image image = new Image();
                image.setLabel(responseBody.get("label").toString());
                image.setConfidence(Double.parseDouble(responseBody.get("confidence").toString()));
                return image;
            } else {
                throw new RuntimeException("Failed to classify image. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error connecting to Flask API", e);
        }
    }
}
