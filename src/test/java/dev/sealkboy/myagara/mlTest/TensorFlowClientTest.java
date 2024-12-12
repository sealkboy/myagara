package dev.sealkboy.myagara.mlTest;

import java.io.File;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import dev.sealkboy.myagara.model.Image;

@Component
public class TensorFlowClientTest {

    private static final String FLASK_URL = "http://localhost:5000/classify";

    /**
     * @param imageFile 
     * @return 
     */
    public Image classifyImage(File imageFile) {
        if (imageFile == null) {
            throw new RuntimeException("File to classify is null");
        }

        if (!imageFile.exists() || !imageFile.isFile()) {
            throw new RuntimeException("File does not exist or is not a valid file: " + imageFile.getPath());
        }

        try {
            RestTemplate restTemplate = new RestTemplate();


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);


            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new FileSystemResource(imageFile));


            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);


            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                FLASK_URL,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );


            if (response.getBody() == null) {
                throw new RuntimeException("Response body is null");
            }

            Map<String, Object> responseBody = response.getBody();

            if (!responseBody.containsKey("label") || !responseBody.containsKey("confidence")) {
                throw new RuntimeException("Response body is missing required keys");
            }

            if (!(responseBody.get("label") instanceof String) || !(responseBody.get("confidence") instanceof Number)) {
                throw new RuntimeException("Unexpected data type in response");
            }

            String label = (String) responseBody.get("label");
            double confidence = ((Number) responseBody.get("confidence")).doubleValue();

            Image image = new Image();
            image.setLabel(label);
            image.setConfidence(confidence);
            return image;

        } catch (Exception e) {
            throw new RuntimeException("Error connecting to Flask API", e);
        }
    }
}