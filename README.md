# G-Scores - High School Graduation Exam Score Portal 🚀

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) <!-- Optional: Add more relevant badges -->

A web application built with Spring Boot and MongoDB to display, search, and analyze Vietnamese High School Graduation Exam scores (Điểm thi THPT). Features include individual score lookup by their registration number, top 10 Group A scores (Maths, Physics, Chemisty) rankings, and score distribution statistics visualized with bar and pie charts.

## ✨ Features

*   **Score Lookup:** Search for individual student scores using their Registration Number (SBD).
*   **Detailed View:** Display all subject scores for a searched student.
*   **Top 10 Rankings:** View the top 10 students based on Group A scores (Maths, Physics, Chemistry).
*   **Score Statistics:** Visualize score distributions for each subject using bar and pie charts (powered by Chart.js).
*   **Data Seeding:** Automatically imports exam score data from a CSV file into MongoDB on the first application startup.
*   **MongoDB Connection Test:** Includes a startup check to verify the MongoDB connection.

## 🛠️ Technologies Used

*   **Backend:**
    *   Java 21
    *   Spring Boot 3.x
    *   Spring Data MongoDB
    *   Spring Web
    *   Maven 3.x
    *   Lombok
    *   OpenCSV (for data import)
*   **Database:**
    *   MongoDB (Tested with MongoDB Atlas and local instances)
*   **Frontend:**
    *   HTML5
    *   CSS3 (Bootstrap 5)
    *   JavaScript (Vanilla JS)
    *   Chart.js (for statistical charts)

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

*   **JDK 21:** Java Development Kit, version 21 or later.
*   **Maven:** Apache Maven 3.6+ for building the project.
*   **MongoDB:** A running MongoDB instance (version 4.4 or later recommended). This can be:
    *   A local MongoDB installation.
    *   A cloud-based instance like [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) (free tier available).
*   **Git:** For cloning the repository.
*   **(Optional) IDE:** An Integrated Development Environment like IntelliJ IDEA, VS Code with Java extensions, or Eclipse.

## 🚀 Getting Started

Follow these steps to get the application running locally:

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/your-username/gscores.git # Replace with your actual repository URL
    cd gscores
    ```

2.  **Prepare the Data File:**
    *   Ensure the exam score CSV file (`diem_thi_thpt_2024.csv`) is placed inside the `src/main/resources/data/` directory. The `DataSeeder` service expects to find it there via classpath resource loading.

3.  **Configure MongoDB Connection:**
    *   Open the configuration file: `src/main/resources/application.properties`
    *   Locate the MongoDB properties:
        ```properties
        # MongoDB
        spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<your-cluster-url>/<your-db-name>?retryWrites=true&w=majority&appName=<YourAppName>
        spring.data.mongodb.database=<your-db-name>
        ```
    *   **IMPORTANT:** Replace the placeholder `spring.data.mongodb.uri` with your **actual MongoDB connection string**.
        *   For **MongoDB Atlas**, copy the connection string provided for applications (ensure you replace `<password>` and potentially `<username>`).
        *   For a **local MongoDB instance**, it might look like `mongodb://localhost:27017/<your-db-name>`.
    *   Set `spring.data.mongodb.database` to the name you want to use for the database (e.g., `gscores_db`). This database will be created automatically if it doesn't exist.
    *   **Security Note:** Avoid committing real credentials directly into the `application.properties` file in a public repository. Use environment variables or external configuration management for production scenarios.

4.  **Build and Run the Application:**

    *   **Option 1: Using Maven Wrapper (Recommended):**
        ```bash
        ./mvnw spring-boot:run
        ```
        *(Use `mvnw.cmd spring-boot:run` on Windows)*

    *   **Option 2: Using Maven Directly:**
        ```bash
        mvn spring-boot:run
        ```

    *   **Option 3: From your IDE:**
        *   Import the project as a Maven project.
        *   Locate the `GscoresApplication.java` class in `src/main/java/com/example/gscores/`.
        *   Right-click and run it as a Java Application.

5.  **Data Seeding on First Run:**
    *   When the application starts for the first time, it will check if the `students` collection in your MongoDB database is empty.
    *   If empty, it will automatically run the `CsvImportService` to parse the `diem_thi_thpt_2024.csv` file and populate the database. This might take a few minutes depending on the file size and your system.
    *   Monitor the console logs for messages like:
        ```
        INFO ... DataSeeder: Collection 'students' is empty. Starting CSV import...
        INFO ... CsvImportServiceImpl: Inserted batch of 10000 records...
        INFO ... CsvImportServiceImpl: Total records imported: XXXXX.
        INFO ... DataSeeder: CSV import completed successfully.
        ```
    *   On subsequent runs, if the `students` collection already contains data, the seeding process will be skipped:
        ```
        INFO ... DataSeeder: Database already seeded. Skipping CSV import.
        ```

6.  **Access the Application:**
    *   Open your web browser and navigate to: `http://localhost:8080`
    *   You should see the G-Scores dashboard interface.

## 🌐 API Endpoints

The application exposes the following RESTful endpoints:

*   `GET /api/scores/{id}`: Get detailed scores for a student by their registration number (SBD). [Search Scores] 
*   `GET /api/scores/statistics`: Get score distribution statistics for all subjects. [Reports]
*   `GET /api/scores/top10`: Get the top 10 students based on Group A scores. [Dashboard]
