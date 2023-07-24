package com.assignment.feliscatus;

import com.assignment.feliscatus.controller.PictureController;
import com.assignment.feliscatus.controller.PictureRepository;
import com.assignment.feliscatus.model.Picture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@RunWith(MockitoJUnitRunner.class)
public class PictureControllerTest {

    @InjectMocks
    private PictureController pictureController;

    @Mock
    private PictureRepository pictureRepository;

    @Test
    public void testUploadPicture() throws IOException {
        MultipartFile file = createMockMultipartFile("test-image.jpg", "image/jpeg", "Test image content".getBytes());
        MultipartFile file1 = Mockito.spy(file);
        doNothing().when(file1).transferTo(any(File.class));
        ResponseEntity<String> response = pictureController.uploadPicture(file1, "This is a test image");

        // Assert the response status and message
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Image uploaded successfully"));
    }

    @Test
    public void testUpdatePicture() {
        Long pictureId = 1L;
        MultipartFile file = createMockMultipartFile("updated-image.jpg", "image/jpeg", "Updated image content".getBytes());
        Picture samplePicture = new Picture(pictureId, "sample-image.jpg", "This is a sample image");
        when(pictureRepository.findById(pictureId)).thenReturn(Optional.of(samplePicture));
        PictureController pictureController1 = Mockito.spy(pictureController);
        doReturn(true).when(pictureController1).updateImageFile(any(), any());
        ResponseEntity<String> response = pictureController1.updatePicture(pictureId, file,  "This is an updated image");

        // Assert the response status and message
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Image updated successfully"));
    }

    @Test
    public void testDeletePicture() {
        Long pictureId = 1L;

        // Create a sample Picture entity for testing
        Picture samplePicture = new Picture();
        samplePicture.setId(pictureId);
        samplePicture.setDescription("This is a sample image");
        samplePicture.setFileName("sample-image.jpg");

        // Mock the behavior of the PictureRepository
        when(pictureRepository.findById(pictureId)).thenReturn(Optional.of(samplePicture));
        PictureController pictureController1 = Mockito.spy(pictureController);
        doReturn(true).when(pictureController1).deleteImageFile(any());
        ResponseEntity<String> response = pictureController1.deletePicture(pictureId);

        // Assert the response status and message
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Image deleted successfully"));
    }

    @Test
    public void testGetPictureById() {
        Long pictureId = 1L;

        // Create a sample Picture entity for testing
        Picture samplePicture = new Picture();
        samplePicture.setId(pictureId);
        samplePicture.setDescription("This is a sample image");
        samplePicture.setFileName("sample-image.jpg");

        // Mock the behavior of the PictureRepository
        when(pictureRepository.findById(pictureId)).thenReturn(Optional.of(samplePicture));

        ResponseEntity<Picture> response = pictureController.getPictureById(pictureId);

        // Assert the response status and the retrieved Picture entity
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Picture retrievedPicture = response.getBody();
        assertNotNull(retrievedPicture);
        assertEquals(samplePicture.getId(), retrievedPicture.getId());
        assertEquals(samplePicture.getDescription(), retrievedPicture.getDescription());
        assertEquals(samplePicture.getFileName(), retrievedPicture.getFileName());
    }

    @Test
    public void testGetAllPictures() {
        // Create a list of sample Picture entities for testing
        List<Picture> samplePictures = new ArrayList<>();
        samplePictures.add(new Picture(1L, "Description 1", "image1.jpg"));
        samplePictures.add(new Picture(2L, "Description 2", "image2.jpg"));

        // Mock the behavior of the PictureRepository
        when(pictureRepository.findAll()).thenReturn(samplePictures);

        ResponseEntity<List<Picture>> response = pictureController.getAllPictures();

        // Assert the response status and the retrieved Picture entities
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Picture> retrievedPictures = response.getBody();
        assertNotNull(retrievedPictures);
        assertEquals(2, retrievedPictures.size());

        // Assert individual picture properties
        assertEquals(samplePictures.get(0).getId(), retrievedPictures.get(0).getId());
        assertEquals(samplePictures.get(0).getDescription(), retrievedPictures.get(0).getDescription());
        assertEquals(samplePictures.get(0).getFileName(), retrievedPictures.get(0).getFileName());

        assertEquals(samplePictures.get(1).getId(), retrievedPictures.get(1).getId());
        assertEquals(samplePictures.get(1).getDescription(), retrievedPictures.get(1).getDescription());
        assertEquals(samplePictures.get(1).getFileName(), retrievedPictures.get(1).getFileName());
    }

    // Helper method to create a mock MultipartFile for testing
    private MultipartFile createMockMultipartFile(String fileName, String contentType, byte[] content) {
        return new MockMultipartFile(fileName, fileName, contentType, content);
    }
}
