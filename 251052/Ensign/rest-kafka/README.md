# REST-Cassandra News Service

A distributed system for managing News and Comments, refactored into a microservices architecture. It uses **PostgreSQL** for the core News/Author/Tag domain and **Cassandra** for the high-volume Comment domain.

## 🏗 Architecture

The project is structured as a multi-module Maven project:

*   **`publisher`**: The main monolythic module (Rest-JPA refactored). Manages Authors, News, and Marks. Backed by PostgreSQL.
    *   Port: `24110`
*   **`discussion`**: A dedicated microservice for managing Comments. Backed by Cassandra.
    *   Port: `24130`

## 🛠 Technology Stack

*   **Java 21**
*   **Spring Boot 3.x**
*   **Databases**:
    *   **PostgreSQL 15** (Publisher service)
    *   **Cassandra 5.0** (Discussion service)
*   **Message Broker**:
    *   **Apache Kafka** (Asynchronous comment transport and moderation)
*   **Liquibase** (Database Migration for both Postgres and Cassandra)
*   **Docker & Docker Compose** (Infrastructure in KRaft mode)
*   **SpringDoc OpenAPI** (Swagger UI)

## 📡 Kafka Integration

The system uses Apache Kafka for asynchronous processing of comments between the `publisher` and `discussion` modules.

### Topics
*   **`InTopic`**: Used by the `publisher` to send new comment requests.
    *   **Partitioning**: Messages are keyed by `newsId` to ensure all comments for the same news item are processed in the same partition.
*   **`OutTopic`**: Used by the `discussion` service to broadcast moderated comment updates back to the `publisher`.

### Moderation Workflow
1.  **Publisher**: Sends a comment creation request to `InTopic` and returns `202 ACCEPTED` with a `PENDING` state.
2.  **Discussion**: Consumes the request, saves the comment to Cassandra, and performs automatic moderation (checks for stop words: "bad", "spam", "illegal").
3.  **Discussion**: Updates the comment state to `APPROVE` or `DECLINE` and sends the result to `OutTopic`.
4.  **Publisher**: Consumes from `OutTopic` to log or further process the result.

### Kafka Configuration
Kafka is configured in **KRaft mode** (no Zookeeper).
- **Bootstrap Servers**: `localhost:9092`
- **Partitions**: 3 (default for both topics)
- **Replication Factor**: 1 (local development)

## 🚀 Getting Started

### Prerequisites

*   Java 21 JDK
*   Maven 3.8+
*   Docker & Docker Compose

### Running the Application

1.  **Start the Infrastructure**:
    Use Docker Compose to spin up PostgreSQL and Cassandra containers.
    ```bash
    docker-compose up -d
    ```
    *   PostgreSQL: `localhost:5432`
    *   Cassandra: `localhost:9042`

2.  **Build the Project**:
    From the root `rest-kafka` directory:
    ```bash
    mvn clean install
    ```

3.  **Run the Services**:
    You need to run both services. You can do this in separate terminal windows.

    *   **Start Discussion Service** (Cassandra):
        ```bash
        cd discussion
        mvn spring-boot:run
        ```
    *   **Start Publisher Service** (Postgres):
        ```bash
        cd publisher
        mvn spring-boot:run
        ```

## 📚 API Documentation

Access the Swagger UI for each service:

*   **Publisher Service**: [http://localhost:24110/swagger-ui/index.html](http://localhost:24110/swagger-ui/index.html)
    *   Manage Authors, News, Marks.
*   **Discussion Service**: [http://localhost:24130/swagger-ui/index.html](http://localhost:24130/swagger-ui/index.html)
    *   Manage Comments.
*   **Kafka-UI**: [http://localhost:8080](http://localhost:8080)
    *   Monitor topics, messages, and consumer groups.

### Key Endpoints

*   **Publisher**:
    *   `GET /api/v1.0/news/{id}`: Retrieves news details **and** fetches associated comments from the Discussion service.
*   **Discussion**:
    *   `POST /api/v1.0/comments`: Add a comment.
    *   `GET /api/v1.0/comments/news/{newsId}`: Get comments for a specific news item.

## 🧪 Testing

The project includes unit and integration tests.

To run all tests:
```bash
mvn test
```

## 📝 Database Schema

*   **PostgreSQL** (`publisher`): `tbl_author`, `tbl_news`, `tbl_mark`, `tbl_news_mark`.
*   **Cassandra** (`discussion`): `tbl_comment`.
