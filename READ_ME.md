
## Picture Management API Documentation

This API documentation provides details about the Picture Management RESTful APIs. These APIs allow users to upload, update, delete, and retrieve pictures with their associated information.

### Base URL

The base URL for all API endpoints is: `http://your-domain.com/api/pictures`

### API Endpoints

1. **Upload a Picture**
    - Endpoint: `POST /api/pictures`
    - Description: Upload a new picture to the server with its associated name and description.
    - Request:
        - Method: `POST`
        - Headers:
            - Content-Type: `multipart/form-data`
        - Parameters:
            - `file`: The image file to be uploaded (required).
            - `name`: The name of the picture (required).
            - `description`: A description of the picture (required).
    - Response:
        - Status: 200 OK
        - Body: Success message with the filename of the uploaded image.

2. **Update a Picture**
    - Endpoint: `PUT /api/pictures/{id}`
    - Description: Update an existing picture's name, description, or image file.
    - Request:
        - Method: `PUT`
        - Headers:
            - Content-Type: `multipart/form-data`
        - Parameters:
            - `file`: The new image file (optional). Leave empty to keep the existing image.
            - `description`: The new description for the picture (required).
    - Response:
        - Status: 200 OK
        - Body: Success message indicating that the picture was updated.

3. **Delete a Picture**
    - Endpoint: `DELETE /api/pictures/{id}`
    - Description: Delete a picture based on its unique ID.
    - Request:
        - Method: `DELETE`
    - Response:
        - Status: 200 OK
        - Body: Success message indicating that the picture was deleted.

4. **Get a Picture by ID**
    - Endpoint: `GET /api/pictures/{id}`
    - Description: Retrieve a picture based on its unique ID.
    - Request:
        - Method: `GET`
    - Response:
        - Status: 200 OK
        - Body: JSON representation of the picture object with its description, and filename.

5. **Get All Pictures**
    - Endpoint: `GET /api/pictures`
    - Description: Retrieve all pictures available in the system.
    - Request:
        - Method: `GET`
    - Response:
        - Status: 200 OK
        - Body: JSON array containing all picture objects, each with its description, and filename.

### Error Handling

The API provides appropriate error messages and status codes for different scenarios:

- 400 Bad Request: Occurs when the request is missing required parameters or the image file is empty during picture upload.
- 404 Not Found: Occurs when trying to retrieve a picture with an invalid ID or when no pictures are available in the system.
- 409 Conflict: Occurs when attempting to upload or update a picture with a filename that already exists.

## Run Spring Boot application
```
mvn spring-boot:run
```