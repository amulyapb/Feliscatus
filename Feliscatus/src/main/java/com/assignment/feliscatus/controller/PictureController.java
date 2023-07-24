package com.assignment.feliscatus.controller;

import com.assignment.feliscatus.model.Picture;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/pictures")
public class PictureController {

  private final PictureRepository pictureRepository;

  @Autowired
  public PictureController(PictureRepository pictureRepository) {
    this.pictureRepository = pictureRepository;
  }

  @Value("${image.upload.directory}")
  private String imageUploadDirectory;

  @Autowired
  public void init() {
    try {
      Files.createDirectories(Paths.get(imageUploadDirectory));
    } catch (IOException e) {
      throw new RuntimeException("Could not initialize folder for upload!");
    }
  }

  // Upload a picture
  @PostMapping
  public ResponseEntity<String> uploadPicture(@RequestParam("file") MultipartFile file,
                                         @RequestParam("description") String description) {

    if (file == null || file.isEmpty()) {
      return ResponseEntity.badRequest().body("Please select a file to upload.");
    }

    // Generate a unique filename for the uploaded image to avoid naming conflicts
    String fileName = file.getOriginalFilename();

    try {
      // Create a File object with the complete path to save the uploaded image
      File imageFile = new File(imageUploadDirectory + File.separator + fileName);

      if (imageFile.exists()) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("A file with the same name already exists.");
      }

      // Save the image to the specified directory
      file.transferTo(imageFile.toPath());

      // Save the filename and description to the database
      Picture picture = new Picture(fileName, description);
      pictureRepository.save(picture);

      return ResponseEntity.ok("Image uploaded successfully. Filename: " + fileName);
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload the image.");
    }
  }


  // Delete a picture
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deletePicture(@PathVariable Long id) {
    // Retrieve the picture entity from the database using the provided ID
    Picture picture = pictureRepository.findById(id).orElse(null);

    if (picture == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Picture with given ID is not found.");
    }

    // Get the filename from the Picture entity
    String fileName = picture.getFileName();

    // Delete the image file from the server's storage
    if (!deleteImageFile(fileName)) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete the image.");
    }

    // Delete the picture entity from the database
    pictureRepository.deleteById(id);

    return ResponseEntity.ok("Image deleted successfully.");
  }

  // Get a picture by ID
  @GetMapping("/{id}")
  public ResponseEntity<Picture> getPictureById(@PathVariable Long id) {
    // Retrieve the picture entity from the database using the provided ID
    Picture picture = pictureRepository.findById(id).orElse(null);

    if (picture == null) {
      return ResponseEntity.notFound().build();
    }

    // If the picture is found, return it in the response
    return ResponseEntity.ok(picture);
  }

  // Get all pictures
  @GetMapping
  public ResponseEntity<List<Picture>> getAllPictures() {
    // Retrieve all picture entities from the database
    Iterable<Picture> pictureIterable = pictureRepository.findAll();

    // Convert the Iterable to a List
    List<Picture> pictures = new ArrayList<>();
    pictureIterable.forEach(pictures::add);

    if (pictures.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    // If pictures are found, return them in the response
    return ResponseEntity.ok(pictures);
  }

  // Update a picture
  @PutMapping("/{id}")
  public ResponseEntity<String> updatePicture(@PathVariable Long id,
                                              @RequestParam("file") MultipartFile file,
                                              @RequestParam("description") String description) {
    // Retrieve the picture entity from the database using the provided ID
    Picture picture = pictureRepository.findById(id).orElse(null);

    if (picture == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Picture with the given ID is not found.");
    }

    // Get the filename from the Picture entity
    String currentFileName = picture.getFileName();

    // Update the picture entity with the new name and description
    picture.setFileName(file.getOriginalFilename());
    picture.setDescription(description);

    // Update the image file on the server's storage
    if (!updateImageFile(currentFileName, file)) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update the image.");
    }

    // Save the updated picture entity to the database
    pictureRepository.save(picture);

    return ResponseEntity.ok("Image updated successfully.");
  }

  public boolean updateImageFile(String currentFileName, MultipartFile newFile) {
    try {
      // Generate a unique filename for the new uploaded image
      String newFileName = newFile.getOriginalFilename();

      // Create a File object with the complete path to save the new uploaded image
      File newImageFile = new File(imageUploadDirectory + File.separator + newFileName);

      // Save the new image to the specified directory
      newFile.transferTo(newImageFile.toPath());

      // Delete the current image file
      deleteImageFile(currentFileName);


      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean deleteImageFile(String fileName) {
    try {
      File imageFile = new File(imageUploadDirectory + File.separator + fileName);

      if (imageFile.exists()) {
        return imageFile.delete();
      }

      return false;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  @PreDestroy
  public void deleteResources() {
    try {
      Iterable<Picture> pictureIterable = pictureRepository.findAll();
      pictureIterable.forEach(picture -> deleteImageFile(picture.getFileName()));
    } catch (Exception e) {
      throw new RuntimeException("Could not delete folder for upload!");
    }
  }
}
