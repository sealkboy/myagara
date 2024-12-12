package dev.sealkboy.myagara.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import dev.sealkboy.myagara.model.Image;
import dev.sealkboy.myagara.service.ImageService;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Image> uploadImage(@RequestParam("image") MultipartFile file) {
        Image uploadedImage = imageService.uploadImage(file);
        return ResponseEntity.ok(uploadedImage);
    }

    @GetMapping
    public ResponseEntity<List<Image>> getAllImages() {
        return ResponseEntity.ok(imageService.getAllImages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable String id) {
        return ResponseEntity.ok(imageService.getImageById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Image> updateImageMetadata(@PathVariable String id, @RequestBody Image updatedImage) {
        Image image = imageService.updateImageMetadata(id, updatedImage);
        return ResponseEntity.ok(image);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteImageById(@PathVariable String id) {
        imageService.deleteImageById(id);
        return ResponseEntity.ok("Image Deleted");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllImages() {
        imageService.deleteAllImages();
        return ResponseEntity.ok("All Images Deleted");
    }
}
