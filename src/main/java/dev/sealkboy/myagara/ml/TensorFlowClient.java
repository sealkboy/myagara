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

    private static final String FLASK_URL = "https://<ngrok-id>.ngrok.io/classify";

    public Image classifyImage(File imageFile) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new FileSystemResource(imageFile));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(FLASK_URL, requestEntity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            Image image = new Image();
            image.setLabel(responseBody.get("label").toString());
            image.setConfidence(Double.parseDouble(responseBody.get("confidence").toString()));
            return image;
        } else {
            throw new RuntimeException("Failed to classify image. Status: " + response.getStatusCode());
        }
    }
}

