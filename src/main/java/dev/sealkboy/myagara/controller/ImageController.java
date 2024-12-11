package dev.sealkboy.myagara.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageReport(@PathVariable String id) {
        return ResponseEntity.ok(imageService.getImageReport(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable String id) {
        imageService.deleteImage(id);
        return ResponseEntity.ok("Image Deleted");
    }
}
