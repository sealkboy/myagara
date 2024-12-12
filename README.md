# Myagara: A Step Towards Fully Automated Precision Agriculture

## Description

Myagara is an innovative project designed to classify plant diseases using machine learning, forming the foundation for a fully automated precision agriculture system. It integrates TensorFlow for disease classification, a Flask API for model hosting, and a Spring Boot backend for data and workflow management. This is version 1.0, aimed at paving the way for scalable and remote agricultural solutions.

## Key Features

- **Plant Disease Classification**: Upload leaf images to classify them into one of 5 disease categories or mark them as healthy.
- **RESTful APIs**: Spring Boot backend provides endpoints to manage image uploads, retrieve images metadata & reports, update images reports & metadata and delete images.
- **Secure Data Storage**: MongoDB stores metadata, classification results, and reports securely and scalably. Its schema-less nature makes it easy to handle dynamic and diverse data.
- **Seamless Flask Integration**: A lightweight Flask server hosts the TensorFlow model and exposes a /classify endpoint. Images uploaded via the Spring Boot backend are sent to the Flask server for classification. The modular design enables better separation of concerns, with the Flask server focusing on AI tasks and the backend managing system operations.

---

## Prerequisites

- Java 17+
- Maven
- MongoDB
- Python 3.9+ (with TensorFlow and Flask installed)
- Postman (for API testing)

---

## Tech Stack

- **Frontend**: Postman (for testing APIs)
- **Backend**: Spring Boot (Java)
- **Machine Learning**: TensorFlow (Python) with Flask
- **Database**: MongoDB
- **Testing**: JUnit (Java), Mockito, and Postman
- **DevOps**: Maven, GitHub

---

## Project Architecture

1. **TensorFlow Model and Flask API**:
   - TensorFlow model trained on the PlantVillage dataset (5 categories).
   - Flask API exposes a `/classify` endpoint for image classification.

2. **Spring Boot Backend**:
   - Manages image uploads, metadata, and classification reports.
   - Communicates with Flask for real-time classification.

3. **MongoDB**:
   - Stores image metadata and classification results.
   - NoSQL database ensures scalability and flexibility for unstructured data.

4. **Testing & DevOps**:
   - Comprehensive tests with JUnit, Mockito, and Postman.
   - Built with Maven and managed using GitHub for version control.

---

## API Documentation

| HTTP Method | Endpoint                       | Description                         | Response Example            |
|-------------|--------------------------------|-------------------------------------|-----------------------------|
| `POST`      | `/api/images/upload`          | Upload an image for classification | `"Image Uploaded"`          |
| `GET`       | `/api/images`                 | Retrieve all classified images     | List of metadata & reports  |
| `GET`       | `/api/images/{id}`            | Retrieve a specific image report   | Metadata & classification   |
| `PUT`       | `/api/images/{id}`            | Update metadata for an image       | Updated metadata            |
| `DELETE`    | `/api/images/{id}`            | Delete a specific image            | `"Image Deleted"`           |
| `DELETE`    | `/api/images`                 | Delete all images                  | `"All Images Deleted"`      |

---

## Project Structure

```plaintext
myagara/
├── src/
│   ├── main/
│   │   ├── java/dev/sealkboy/myagara/
│   │   │   ├── controller/
│   │   │   │   ├── ImageController.java
│   │   │   ├── ml/
│   │   │   │   ├── TensorFlowClient.java
│   │   │   ├── model/
│   │   │   │   ├── Image.java
│   │   │   ├── repository/
│   │   │   │   ├── ImageRepository.java
│   │   │   ├── service/
│   │   │   │   ├── ImageService.java
│   │   │   ├── MyagaraApplication.java
│   │   ├── resources/
│   │   │   ├── application.properties
│   ├── test/
│       ├── java/dev/sealkboy/myagara/
│           ├── controller/
│           │   ├── ImageControllerTest.java
│           ├── ml/
│           │   ├── TensorFlowClientTest.java
│           ├── service/
│               ├── ImageServiceTest.java
├── pom.xml
├── README.md
```

---

## How to Run the Project

### 1. Training the Model
   - Open the myagara_model.py file in Spyder or another Python IDE (not Visual Studio Code, due to compatibility issues with TensorFlow/Keras in VSC).
   - Ensure you have installed the necessary Python dependencies, including TensorFlow and Keras.
   - Execute the script to train the TensorFlow model using the PlantVillage dataset. The script will generate a model file named myagara_final_model.h5.
   - Save the myagara_final_model.h5 in a directory accessible by the Flask server.

Note: Running the model training in Visual Studio Code can cause issues due to conflicts between TensorFlow/Keras and the Python environment. Using Spyder ensures smooth execution.

### 2. Running the Flask Server
   - Navigate to the project directory containing `myagara_flask_server.py`.
   - Use Spyder or the command line to run Flask server.
   - Ensure you have installed Flask and other dependencies.
   - Run the Flask server.
   - Ensure the Flask server is running at `http://localhost:5000/classify`.

### 3. MongoDB Setup
   - Start the MongoDB server.
   - Open MongoDB Compass to visually manage the database.
   - Verify that the `application.properties` file in the Spring Boot backend contains the correct MongoDB connection string, such as:
     ```
     spring.data.mongodb.uri=mongodb://localhost:27017/myagara
     ```

### 4. Spring Boot Backend
   - Build the backend using Maven:
     ```
     mvn clean install
     ```
   - Open the project in Visual Studio Code or your preferred IDE.
   - Run the `MyagaraApplication.java` file to start the Spring Boot application.
   - The backend will start on `http://localhost:8080`.

### 5. Testing the Application
#### a. Testing with Maven
   - Run all unit tests using:
     ```
     mvn test
     ```
   - Confirm that all tests pass and validate functionality.

#### b. Testing with Postman
   - Use Postman to test the API endpoints:
     - **Upload Image**:
       ```
       POST /api/images/upload
       ```
       - Select an image to upload and verify the response is "Image Uploaded."
     - **Get All Images**:
       ```
       GET /api/images
       ```
       - Verify that the response includes metadata and classification results for all images.
     - **Get Image by ID**:
       ```
       GET /api/images/{id}
       ```
       - Verify that the correct image metadata and classification results are returned.
     - **Delete Image by ID**:
       ```
       DELETE /api/images/{id}
       ```
       - Verify that the response is "Image Deleted."

#### c. Checking MongoDB Persistence
   - Open MongoDB Compass.
   - Navigate to the `myagara` database and the `images` collection.
   - Verify that all uploaded images' metadata and classification results are stored correctly.

### 6. Workflow Recap
   - **Train the Model**: Use Python and TensorFlow to generate the `myagara_final_model.h5`.
   - **Run Flask Server**: Host the classification model on a local Flask server.
   - **Run Spring Boot Backend**: Start the backend to handle API requests and communicate with Flask and MongoDB.
   - **Test**: Use Maven for unit testing, Postman for API validation, and MongoDB Compass for data persistence verification.

---

## Contact Information

If you have any questions, suggestions, or would like to contribute to the project, feel free to reach out:

    - GitHub: https://github.com/sealkboy
    - LinkedIn: https://www.linkedin.com/in/sealkboy
