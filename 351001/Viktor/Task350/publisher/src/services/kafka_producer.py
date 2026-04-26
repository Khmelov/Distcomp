from aiokafka import AIOKafkaProducer
import json
import asyncio

class KafkaProducerService:
    def __init__(self, bootstrap_servers='127.0.0.1:9092'):
        self.producer = AIOKafkaProducer(
            bootstrap_servers=bootstrap_servers,
            value_serializer=lambda v: json.dumps(v).encode()
        )

    async def start(self):
        await self.producer.start()

    async def stop(self):
        await self.producer.stop()

    async def send_request(self, topic: str, key: str, value: dict):
        # key = tweet_id (как строка) для гарантии одной партиции
        await self.producer.send(topic, key=key.encode(), value=value)