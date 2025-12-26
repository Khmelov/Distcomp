# REST-JPA News Service

A Spring Boot application providing a RESTful API for managing News, Authors, Comments, and Tags (Marks). 
This project has been refactored to use PostgreSQL, Spring Data JPA, and Liquibase for database management.

## 🛠 Technology Stack

*   **Java 21**
*   **Spring Boot 3.x** (Web, Data JPA, Validation)
*   **PostgreSQL 15** (Database)
*   **Liquibase** (Database Migration)
*   **MapStruct** (Dto-Entity Mapping)
*   **Docker & Docker Compose** (Containerization)
*   **SpringDoc OpenAPI** (Swagger UI)

## 🚀 Getting Started

### Prerequisites

*   Java 21 JDK
*   Maven 3.8+
*   Docker & Docker Compose

### Running the Application

1.  **Start the Database**:
    Use Docker Compose to spin up the PostgreSQL container.
    ```bash
    docker-compose up -d
    ```

2.  **Build the Application**:
    ```bash
    mvn clean install
    ```

3.  **Run the Application**:
    ```bash
    mvn spring-boot:run
    ```

The application will start on `http://localhost:8080`.

## 📚 API Documentation

Interactive API documentation (Swagger UI) is available at:

*   **URL**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
*   **JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Main Resources
All API endpoints are prefixed with `/api/v1.0`.

*   `/api/v1.0/authors` - Manage Authors
*   `/api/v1.0/news` - Manage News articles
*   `/api/v1.0/comments` - Manage Comments on news
*   `/api/v1.0/marks` - Manage Marks (Tags)

## 🧪 Testing

The project uses **TestContainers** for integration testing with a real PostgreSQL instance.

To run tests:
```bash
mvn test
```

## 📝 Database Schema

The database schema is managed via Liquibase changelogs located in `src/main/resources/db/changelog`.

*   `tbl_author`: Stores author information.
*   `tbl_news`: Stores news articles, linked to authors.
*   `tbl_comment`: Stores comments linked to news.
*   `tbl_mark`: Stores tags/marks.
*   `tbl_news_mark`: Join table for News-Mark many-to-many relationship.
