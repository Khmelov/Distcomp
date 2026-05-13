# MyStartRestApplication

A Spring Boot-based REST application consisting of two core services: `publisher` and `discussion`, organized in a Maven monorepo.

## Project Overview

- **Architecture:** Monorepo with two independent Spring Boot services.
- **Publisher Service:** Manages users, articles, and labels. Uses **PostgreSQL** for persistence and **Redis** for caching notices.
- **Discussion Service:** Manages notices (comments) for articles. Uses **Cassandra** for persistence.
- **Inter-service Communication:** The `publisher` service communicates with the `discussion` service via **Kafka** using a request-reply pattern for most operations and an asynchronous creation flow.

## Technologies

- **Language:** Java 21
- **Framework:** Spring Boot 3.5.6
- **Messaging:** Apache Kafka (Spring Kafka) with `ReplyingKafkaTemplate`.
- **Database (Publisher):** PostgreSQL (for core data and notice ID generation).
- **Database (Discussion):** Cassandra (via JDBC Cassandra Driver for Liquibase support).
- **Caching:** Redis (Reactive Redis Template in Publisher).
- **Migrations:** Liquibase (with Cassandra extension for Discussion).
- **Mapping:** MapStruct.
- **Utilities:** Lombok, QueryDSL (for dynamic filtering in Publisher), TSID (Hypersistence).
- **Reactive Programming:** Project Reactor (Mono/Flux) is used in the Publisher's integration layer for non-blocking Kafka communication.
- **Configuration:** `springboot3-dotenv` for environment variable support.

## Notice Lifecycle (Kafka Integration)

The `publisher` service acts as a gateway for notices, delegating storage and moderation to the `discussion` service.

- **Creation (POST):**
    1. `publisher` verifies the existence of the article.
    2. `publisher` generates a unique ID using a PostgreSQL sequence (`notice_id_seq`).
    3. `publisher` sends a `KafkaNoticeMessage` (Operation: `POST`) to `InTopic`. The `articleId` is used as the Kafka message key for partitioning.
    4. `publisher` returns the notice to the client immediately with state `PENDING`.
    5. `discussion` consumes the message, performs auto-moderation (stop-words filter), updates state to `APPROVE` or `DECLINE`, and saves to Cassandra.
- **Queries and Updates (GET/PUT/DELETE):**
    1. `publisher` sends a request to `InTopic` using `ReplyingKafkaTemplate`.
    2. `publisher` blocks (non-blockingly via `Mono`) for up to 1 second waiting for a response on `OutTopic`.
    3. `discussion` processes the request and sends a correlated `KafkaNoticeResponseMessage` to `OutTopic`.
    4. `publisher` receives the response, handles caching in **Redis**, and either returns the data or throws a mapped exception (e.g., `MyEntityNotFoundException`).

## Building and Running

### Prerequisites
- Java 21
- Maven
- PostgreSQL, Cassandra, Kafka, and Redis instances running (as configured in `.env` or `application.yaml`).

### Commands
- **Build entire project:**
  ```bash
  mvn clean install
  ```
- **Run Publisher Service:**
  ```bash
  mvn spring-boot:run -pl publisher/publisher-impl
  ```
- **Run Discussion Service:**
  ```bash
  mvn spring-boot:run -pl discussion/discussion-impl
  ```
- **Run Tests:**
  ```bash
  mvn test
  ```

## Development Conventions

### API Structure
- Base paths are versioned (e.g., `/api/v1.0`, `/api/v2.0`).
- Publisher controllers for core entities (Article, User, Label) are standard Spring MVC.
- Publisher controller for Notices (`NoticeIntegrationController`) uses **WebFlux (Mono)** for async Kafka integration.

### Data Transfer Objects (DTOs)
- Request objects: `*RequestTo` (e.g., `ArticleRequestTo`).
- Response objects: `*ResponseTo` (e.g., `ArticleResponseTo`).
- `KafkaNoticeMessage` and `KafkaNoticeResponseMessage` are used for cross-service communication.

### Persistence & Schema
- **Publisher:** Uses Spring Data JPA and QueryDSL. Schema managed by Liquibase in `publisher-impl`.
- **Discussion:** Uses Spring Data Cassandra. Primary key for notices is a composite key `((country, article_id), id)`. Schema managed by Liquibase using a Cassandra JDBC wrapper.

### Error Handling
- Global exception handling is implemented via `@RestControllerAdvice` in `GlobalExceptionHandler` within each module.
- Errors from the `discussion` service are propagated back to `publisher` via Kafka and re-thrown as local exceptions.
