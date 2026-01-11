#!/bin/bash

echo "Starting Redis for Publisher module..."

# Проверяем, запущен ли уже Redis
if docker ps | grep -q "publisher-redis"; then
    echo "Redis is already running"
else
    echo "Starting Redis container..."
    docker run -d \
        --name publisher-redis \
        -p 6379:6379 \
        -v publisher-redis-data:/data \
        redis:7-alpine \
        redis-server --appendonly yes --maxmemory 512mb --maxmemory-policy allkeys-lru
    
    echo "Waiting for Redis to start..."
    sleep 5
    
    echo "Redis started successfully!"
    echo "Host: localhost"
    echo "Port: 6379"
fi

# Проверяем подключение
echo "Testing Redis connection..."
docker exec publisher-redis redis-cli ping

echo "Redis is ready for Publisher module!"