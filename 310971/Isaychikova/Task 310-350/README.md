# Distributed Computing Project (Publisher + Discussion)

Multi-module Spring Boot 3 (Java 17) project with two microservices:
- **publisher** (port 24110): JPA/Postgres + REST + proxy to discussion
- **discussion** (port 24130): Cassandra + REST

## Architecture
- `publisher` stores `Writer`, `Article`, `Sticker` in Postgres (JPA).
- `discussion` stores `Post` in Cassandra (`distcomp.tbl_post`).
- `publisher` forwards all `Post` CRUD to `discussion` via REST.

## Build & Run (no external Maven required)

### Build / test
```powershell
.\mvnw.cmd test
```
- Tests use H2 (publisher) and mocked Cassandra (discussion) – no running DBs required.

### Run services
1) **Cassandra** must be running on `localhost:9042`.
2) (Optional) Recreate keyspace/table:
   ```sql
   DROP TABLE IF EXISTS distcomp.tbl_post;
   ```
3) Run services:
```powershell
.\mvnw.cmd -pl discussion spring-boot:run
.\mvnw.cmd -pl publisher spring-boot:run
```

## API (prefix `/api/v1.0`)

### Publisher (24110)
- `GET /writers` – list writers
- `POST /writers` – create writer
- `GET /writers/{id}`, `PUT /writers/{id}`, `DELETE /writers/{id}`
- `GET /stickers`, `POST /stickers`, `GET /stickers/{id}`, `PUT /stickers/{id}`, `DELETE /stickers/{id}`
- `GET /articles`, `POST /articles`, `GET /articles/{id}`, `PUT /articles/{id}`, `DELETE /articles/{id}`
- **Posts (proxy to discussion):**
  - `GET /posts` – list all posts
  - `POST /posts` – create post (body: `articleId`, `content`; optional `id`, `country`)
  - `GET /posts/{id}`, `PUT /posts/{id}`, `DELETE /posts/{id}`

### Discussion (24130)
- `GET /posts` – list all posts
- `POST /posts` – create post
- `GET /posts/{id}`, `PUT /posts/{id}`, `DELETE /posts/{id}`
- Optional query filter: `GET /posts?country=by&articleId=123` (list by article)

## Data Model

### Publisher (Postgres)
- `tbl_writer`: id, login, password, firstname, lastname
- `tbl_article`: id, writer_id, title, content, created, modified
- `tbl_sticker`: id, name
- `tbl_article_sticker`: article_id, sticker_id (many-to-many)

### Discussion (Cassandra)
Keyspace: `distcomp`
Table: `tbl_post`
```
country text,
article_id bigint,
id text,
content text,
PRIMARY KEY ((country), article_id, id)
```
- `id` is a string (UUID) – matches test expectations.
- Default `country` = `by` if not provided.
- `ALLOW FILTERING` used for lookups by `id` only.

## Test Compatibility
- All `Post` endpoints match the external checker contract:
  - No required query parameters for `GET /posts`.
  - CRUD via `/posts/{id}` with string `id`.
  - `POST` accepts `articleId` and `content` (country optional).

## Notes
- Maven Wrapper (`mvnw.cmd`/`mvnw`) included – no global Maven needed.
- Spring Boot profiles: default config in `application.yml`.
- `discussion` uses `schema-action: recreate_drop_unused` for dev/test.
