package dev.sealkboy.myagara.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
        imageService.uploadImage(file);
        return ResponseEntity.ok("Image Uploaded");
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
