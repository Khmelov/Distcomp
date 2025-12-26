# News Management System REST API

## Overview
This is a RESTful API application for a News Management System, built with **Java 21** and **Spring Boot 3.4.1**. It manages entities such as **Authors**, **News**, **Marks**, and **Comments**.

The application uses an in-memory storage implementation for simplicity and ease of testing.

## Technology Stack
- **Java 21**
- **Spring Boot 3.4.1** (Web, Test)
- **Maven**
- **JUnit 5** & **RestAssured** (Testing)
- **SpringDoc OpenAPI** (Swagger Documentation)

## Getting Started

### Prerequisites
- JDK 21 or higher
- Maven 3.6+

### Build
Navigate to the project directory and build using Maven:

```bash
mvn clean install
```

### Run
Run the application using the Spring Boot Maven plugin:

```bash
mvn spring-boot:run
```

 The application will start on port `24110`.

## API Documentation
The application integrates **Swagger UI** for interactive API documentation.
- **Swagger UI**: [http://localhost:24110/swagger-ui/index.html](http://localhost:24110/swagger-ui/index.html)
- **OpenAPI JSON**: [http://localhost:24110/v3/api-docs](http://localhost:24110/v3/api-docs)

The base URL for all API endpoints is `http://localhost:24110/api/v1.0`.

### Endpoints

#### Authors (`/authors`)
- `GET /` - Retrieve all authors
- `GET /{id}` - Retrieve an author by ID
- `POST /` - Create a new author
- `PUT /{id}` - Update an existing author
- `DELETE /{id}` - Delete an author

#### News (`/news`)
- `GET /` - Retrieve all news
- `GET /{id}` - Retrieve news by ID
- `POST /` - Create a new news entry
- `PUT /{id}` - Update a news entry
- `DELETE /{id}` - Delete a news entry

#### Comments (`/comments`)
- `GET /` - Retrieve all comments
- `GET /{id}` - Retrieve a comment by ID
- `POST /` - Create a new comment (linked to News)
- `PUT /{id}` - Update a comment
- `DELETE /{id}` - Delete a comment

#### Marks (`/marks`)
- `GET /` - Retrieve all marks
- `GET /{id}` - Retrieve a mark by ID
- `POST /` - Create a new mark
- `PUT /{id}` - Update a mark
- `DELETE /{id}` - Delete a mark

## Testing
Run the automated integration tests using:

```bash
mvn test
```
