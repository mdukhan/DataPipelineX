# Movie Service API Documentation

## Overview

This project provides a Movie Service API for managing movies. It includes functionality to upload movies from a CSV file, add, update, delete, and retrieve movies based on specified genres or titles. The service leverages Spring Boot and Kafka for communication.

## Usage

### 1. Upload Movies from CSV

#### Endpoint
`POST /api/upload-csv-file`

#### Request
- Method: POST
- Parameters: `file` (MultipartFile) - CSV file containing movie data.

#### Response
- Status 201 (Created): Successful file processing.
- Status 400 (Bad Request): Missing file or invalid file format.
- Status 500 (Internal Server Error): Error processing the file.

### 2. Add Movie

#### Endpoint
`POST /add/movie`

#### Request
- Method: POST
- Body: Movie object to be added.
  
#### Response
- Status 201 (Created): Movie added successfully.
- Status 400 (Bad Request): Missing MovieId.
- Status 500 (Internal Server Error): Error processing the request.

### 3. Update Movie

#### Endpoint
`PUT /update/movie`

#### Request
- Method: PUT
- Body: Movie object to be updated.
  
#### Response
- Status 200 (OK): Movie updated successfully.
- Status 400 (Bad Request): Missing MovieId.
- Status 500 (Internal Server Error): Error processing the request.

### 4. Delete Movie

#### Endpoint
`DELETE /delete/movie`

#### Request
- Method: DELETE
- Body: Movie object to be deleted.
  
#### Response
- Status 202 (Accepted): Movie deleted successfully.
- Status 400 (Bad Request): Missing MovieId.
- Status 500 (Internal Server Error): Error processing the request.

### 5. Search Movies by Genres

#### Endpoint
`GET /get/movies/genres/{genres}`

#### Request
- Method: GET
- Path Variable: `genres` - Genres to search for.

#### Response
- Status 200 (OK): JSON list of movies matching the given genres.
- Status 500 (Internal Server Error): Error processing the request.

### 6. Search Movies by Title

#### Endpoint
`GET /get/movies/title/{title}`

#### Request
- Method: GET
- Path Variable: `title` - Title to search for.

#### Response
- Status 200 (OK): JSON list of movies matching the given title.
- Status 500 (Internal Server Error): Error processing the request.

## Dependencies

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Kafka](https://kafka.apache.org/)
- [Java](https://www.java.com/)

## Authors

- mdukhan

## License

This project is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) - see the [LICENSE.md](LICENSE.md) file for details.
