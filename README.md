# SWIFT Code Management Application

## Overview

This application allows users to manage SWIFT codes, including parsing the data from a provided file, storing it in a database, and exposing it through a set of RESTful APIs. It is designed to support quick, low-latency queries for retrieving SWIFT code details and storing them in a structured way.

## Features

1. **Parse SWIFT codes data**: The application reads a provided file, parses the SWIFT codes, and stores them in a database.
   - SWIFT codes ending with "XXX" are recognized as headquarters.
   - Branch codes are associated with headquarters based on their first 8 characters.
   - Country codes and names are stored in uppercase.
   
2. **Database Storage**: The application stores SWIFT codes in a database, ensuring fast and low-latency queries for swift code retrieval and country-specific data.
   
3. **RESTful API Endpoints**:
   - **GET /v1/swift-codes/{swift-code}**: Retrieve details of a single SWIFT code (either headquarter or branch).
   - **GET /v1/swift-codes/country/{countryISO2code}**: Retrieve all SWIFT codes for a specific country (both headquarter and branch codes).
   - **POST /v1/swift-codes**: Add a new SWIFT code entry to the database.
   - **DELETE /v1/swift-codes/{swift-code}**: Delete a SWIFT code entry from the database.

## Setup Instructions

### Prerequisites

- Gradle
- Docker
- Docker Compose

### Steps to Run the Application

1. Clone the repository or navigate to the project folder:
   ```bash
   cd swiftcodes
   ```
2. Build and run the application using Docker:
   ```bash
   gradle build
   docker build -t swiftcodes-app .
   docker-compose up --build
   ```
3. The application should now be running in a Docker container. The server will be accessible at **http://127.0.0.1:8080**

## Loading Data from CSV
To load the data from the provided SWIFT code CSV file, follow these steps:

Make sure the Docker container is running.

In your browser or Postman, send a POST request to the following endpoint: **http://127.0.0.1:8080/swift/import**


## Available Endpoints

### GET /v1/swift-codes/{swift-code}
Retrieves details of a single SWIFT code.

- **Example Request**:  
  `GET /v1/swift-codes/AIZKLV22XXX`

### GET /v1/swift-codes/country/{countryISO2code}
Retrieves all SWIFT codes for a specific country, including both headquarters and branch codes.

- **Example Request**:  
  `GET /v1/swift-codes/country/PL`

### POST /v1/swift-codes
Adds a new SWIFT code entry to the database.

- **Example Request Body**:
  ```json
  {
    "address": "Addess, TownName, Number",
    "bankName": "Bank Name",
    "countryISO2": "PL",
    "countryName": "Poland",
    "isHeadquarter": true,
    "swiftCode": "ABCABCABCAB"
  }

### DELETE /v1/swift-codes/{swift-code}
Deletes a SWIFT code entry from the database.

- **Example Request**:  
  `DELETE /v1/swift-codes/ABCABCABCAB`
