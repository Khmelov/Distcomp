# Redis Setup Instructions

This document provides instructions for setting up Redis using Docker for the BlogPlatform application.

## Prerequisites

- Docker installed and running
- Docker Compose (optional, but recommended)

## Quick Start

### 1. Start Redis Container

Run the following command to start a Redis container:

```bash
docker run -d --name redis -p 6379:6379 redis:latest
```

Or using Docker Compose (create `docker-compose-redis.yml`):

```yaml
version: '3.8'
services:
  redis:
    image: redis:latest
    container_name: redis
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis-data:/data

volumes:
  redis-data:
```

Then run:
```bash
docker-compose -f docker-compose-redis.yml up -d
```

### 2. Verify Redis is Running

Check if Redis container is running:

```bash
docker ps | grep redis
```

You should see the Redis container in the list.

### 3. Test Redis Connection

Test the connection using Redis CLI:

```bash
docker exec -it redis redis-cli ping
```

You should receive `PONG` as a response.

### 4. View Redis Logs

To view Redis logs:

```bash
docker logs redis
```

## Configuration

The application is configured to connect to Redis at:
- **Host**: `localhost`
- **Port**: `6379`

These settings can be changed in `publisher/src/main/resources/application.properties`:

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=2000ms
spring.cache.type=redis
spring.cache.redis.time-to-live=600000
spring.cache.redis.cache-null-values=false
```

## Cache Configuration

The application uses the following cache names:
- `posts` - Caches Post entities
- `postsByArticle` - Caches posts by article ID
- `articles` - Caches Article entities
- `articlesByUser` - Caches articles by user ID
- `articlesByLabel` - Caches articles by label
- `articlesByFilter` - Caches filtered articles
- `users` - Caches User entities
- `labels` - Caches Label entities

Cache TTL (Time To Live) is set to 10 minutes by default.

## Stopping Redis

To stop the Redis container:

```bash
docker stop redis
```

To remove the Redis container:

```bash
docker rm redis
```

## Troubleshooting

### Redis Connection Refused

If you get a connection refused error:
1. Verify Redis container is running: `docker ps | grep redis`
2. Check if port 6379 is available: `netstat -an | grep 6379` (Windows) or `lsof -i :6379` (Linux/Mac)
3. Verify Redis is accessible: `docker exec -it redis redis-cli ping`

### Cache Not Working

If caching doesn't seem to work:
1. Check application logs for Redis connection errors
2. Verify Redis is running and accessible
3. Check that `@EnableCaching` is present in `RedisConfig`
4. Verify cache annotations are correctly placed on service methods

## Additional Resources

- [Redis Documentation](https://redis.io/documentation)
- [Spring Data Redis Documentation](https://docs.spring.io/spring-data/redis/docs/current/reference/html/)

