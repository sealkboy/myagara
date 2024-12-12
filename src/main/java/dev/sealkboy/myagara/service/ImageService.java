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
     * Uploads an image, classifies it using the ML model, and persists the data.
     *
     * @param file MultipartFile representing the uploaded image.
     * @return The saved Image object with metadata and classification results.
     */
    public Image uploadImage(MultipartFile file) {
        try {
            // Save the image locally
            String filePath = saveImageLocally(file);

            // Classify the image using the TensorFlow client
            Image image = tensorFlowClient.classifyImage(new File(filePath));

            // Populate additional metadata
            image.setId(UUID.randomUUID().toString());
            image.setFilename(file.getOriginalFilename());
            image.setTimestamp(System.currentTimeMillis());

            // Persist the image metadata and report to MongoDB
            return imageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading and processing the image", e);
        }
    }

    /**
     * Retrieves all images from the database.
     *
     * @return List of all Image objects.
     */
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    /**
     * Retrieves a single image by its ID.
     *
     * @param id The ID of the image.
     * @return The Image object if found.
     */
    public Image getImageById(String id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + id));
    }

    /**
     * Updates the metadata of an existing image.
     *
     * @param id           The ID of the image to update.
     * @param updatedImage The updated Image object containing the new metadata.
     * @return The updated Image object.
     */
    public Image updateImageMetadata(String id, Image updatedImage) {
        // Find the existing image
        Image existingImage = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + id));

        // Update fields if they are not null or zero
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

        // Save and return the updated image
        return imageRepository.save(existingImage);
    }

    /**
     * Deletes an image by its ID.
     *
     * @param id The ID of the image to delete.
     */
    public void deleteImageById(String id) {
        if (!imageRepository.existsById(id)) {
            throw new RuntimeException("Image not found with ID: " + id);
        }
        imageRepository.deleteById(id);
    }

    /**
     * Deletes all images from the database.
     */
    public void deleteAllImages() {
        imageRepository.deleteAll();
    }

    /**
     * Saves an uploaded image to the local file system.
     *
     * @param file MultipartFile representing the uploaded image.
     * @return The file path of the saved image.
     * @throws IOException If an error occurs while saving the file.
     */
    private String saveImageLocally(MultipartFile file) throws IOException {
        String directoryPath = "C:/tmp"; // Adjust this path based on your OS
        File directory = new File(directoryPath);

        // Ensure the directory exists
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create directory: " + directoryPath);
        }

        // Save the file
        String filePath = directoryPath + "/" + file.getOriginalFilename();
        file.transferTo(new File(filePath));
        return filePath;
    }
}