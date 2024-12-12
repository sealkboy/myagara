package dev.sealkboy.myagara.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
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

    /**
     * @param file 
     * @return 
     */
    public Image uploadImage(MultipartFile file) {
        try {
            String filePath = saveImageLocally(file);

            Image image = tensorFlowClient.classifyImage(new File(filePath));

            image.setId(UUID.randomUUID().toString());
            image.setFilename(file.getOriginalFilename());
            image.setTimestamp(System.currentTimeMillis());

            return imageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading and processing the image", e);
        }
    }

    /**
     * @return
     */
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    /**
     * @param id 
     * @return 
     */
    public Image getImageById(String id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + id));
    }

    /**
     * @param id           
     * @param updatedImage 
     * @return 
     */
    public Image updateImageMetadata(String id, Image updatedImage) {
        Image existingImage = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + id));

        if (updatedImage.getFilename() != null) {
            existingImage.setFilename(updatedImage.getFilename());
        }
        if (updatedImage.getTimestamp() != 0) {
            existingImage.setTimestamp(updatedImage.getTimestamp());
        }
        if (updatedImage.getLabel() != null) {
            existingImage.setLabel(updatedImage.getLabel());
        }
        if (updatedImage.getConfidence() != 0) {
            existingImage.setConfidence(updatedImage.getConfidence());
        }

        return imageRepository.save(existingImage);
    }

    /**
    @param id 
     */
    public void deleteImageById(String id) {
        if (!imageRepository.existsById(id)) {
            throw new RuntimeException("Image not found with ID: " + id);
        }
        imageRepository.deleteById(id);
    }

    public void deleteAllImages() {
        imageRepository.deleteAll();
    }

    /**
     * @param file 
     * @return 
     * @throws IOException 
     */
    private String saveImageLocally(MultipartFile file) throws IOException {
        String directoryPath = "C:/tmp"; 
        File directory = new File(directoryPath);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create directory: " + directoryPath);
        }

        String filePath = directoryPath + "/" + file.getOriginalFilename();
        file.transferTo(new File(filePath));
        return filePath;
    }
}