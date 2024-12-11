package dev.sealkboy.myagara.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dev.sealkboy.myagara.ml.TensorFlowClient;
import dev.sealkboy.myagara.model.Image;
import dev.sealkboy.myagara.repository.ImageRepository;

@Service
public class ImageService {

    @Autowired
    private TensorFlowClient tensorFlowClient;

    @Autowired
    private ImageRepository imageRepository;

    public void uploadImage(MultipartFile file) {
        try {
            String filePath = saveImageLocally(file);
            Image image = tensorFlowClient.classifyImage(new File(filePath));
            image.setId(UUID.randomUUID().toString());
            image.setFilename(file.getOriginalFilename());
            image.setTimestamp(System.currentTimeMillis());
            imageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image", e);
        }
    }

    public Image getImageReport(String id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }

    public void deleteImage(String id) {
        imageRepository.deleteById(id);
    }

    private String saveImageLocally(MultipartFile file) throws IOException {
        String filePath = "/tmp/" + file.getOriginalFilename();
        file.transferTo(new File(filePath));
        return filePath;
    }
}
