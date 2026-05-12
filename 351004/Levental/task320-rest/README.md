# Task310 REST

Spring Boot REST API for entities Creator, Article, Sticker, Notice.

## Stack
- Java 21
- Spring Boot 3
- Maven
- MapStruct
- JUnit 5
- RestAssured

## Run
```bash
mvn spring-boot:run
```

Application runs on:
```text
http://localhost:24110
```

## API prefix
```text
/api/v1.0
```

## Main endpoints
- `POST /api/v1.0/creators`
- `GET /api/v1.0/creators`
- `GET /api/v1.0/creators/{id}`
- `PUT /api/v1.0/creators/{id}`
- `DELETE /api/v1.0/creators/{id}`
- `GET /api/v1.0/creators/byArticle/{articleId}`

- `POST /api/v1.0/articles`
- `GET /api/v1.0/articles`
- `GET /api/v1.0/articles/{id}`
- `PUT /api/v1.0/articles/{id}`
- `DELETE /api/v1.0/articles/{id}`

- `POST /api/v1.0/stickers`
- `GET /api/v1.0/stickers`
- `GET /api/v1.0/stickers/{id}`
- `PUT /api/v1.0/stickers/{id}`
- `DELETE /api/v1.0/stickers/{id}`
- `GET /api/v1.0/stickers/byArticle/{articleId}`

- `POST /api/v1.0/notices`
- `GET /api/v1.0/notices`
- `GET /api/v1.0/notices/{id}`
- `PUT /api/v1.0/notices/{id}`
- `DELETE /api/v1.0/notices/{id}`
- `GET /api/v1.0/notices/byArticle/{articleId}`
