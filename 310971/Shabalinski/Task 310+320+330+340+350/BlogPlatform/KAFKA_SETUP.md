# Kafka Setup Instructions

## Prerequisites
- Docker installed and running

## Step 1: Create Docker Network
```bash
docker network create kafkanet
```

## Step 2: Start Zookeeper
```bash
docker run -d --network=kafkanet --name=zookeeper -e \
ZOOKEEPER_CLIENT_PORT=2181 -e ZOOKEEPER_TICK_TIME=2000 -p 2181:2181 \
confluentinc/cp-zookeeper
```

## Step 3: Start Kafka
```bash
docker run -d --network=kafkanet --name=kafka -e \
KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -e \
KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 -e \
KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 -p 9092:9092 \
confluentinc/cp-kafka:7.4.0
```

**Note:** Using version 7.4.0 to avoid compatibility issues with newer Kafka versions that require additional configuration.

## Step 4: Verify Kafka is Running
```bash
docker ps | grep kafka
docker ps | grep zookeeper
```

## Step 5: Create Topics (Optional - Spring will create them automatically)
```bash
docker exec -it kafka kafka-topics --create --topic InTopic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
docker exec -it kafka kafka-topics --create --topic OutTopic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

## Topics
- **InTopic**: Used by publisher to send post creation requests to discussion
- **OutTopic**: Used by discussion to send post creation responses back to publisher

## Partitioning
Messages are partitioned by `articleId` to ensure all posts for the same article go to the same partition.

## Stopping Kafka
```bash
docker stop kafka zookeeper
docker rm kafka zookeeper
```

