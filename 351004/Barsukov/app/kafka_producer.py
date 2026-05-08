from aiokafka import AIOKafkaProducer
import json
import asyncio
from kafka_config import KAFKA_BOOTSTRAP_SERVERS

class KafkaProducer:
    def __init__(self):
        self.producer = None

    async def start(self):
        self.producer = AIOKafkaProducer(
            bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
            value_serializer=lambda v: json.dumps(v).encode('utf-8')
        )
        await self.producer.start()

    async def stop(self):
        if self.producer:
            await self.producer.stop()

    async def send_message(self, topic: str, key: str, value: dict):
        """Отправка сообщения с ключом для гарантии попадания в одну партицию"""
        if self.producer:
            await self.producer.send(topic, key=key.encode('utf-8'), value=value)


# Глобальный экземпляр
kafka_producer = KafkaProducer()
