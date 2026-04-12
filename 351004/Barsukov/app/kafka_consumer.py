from aiokafka import AIOKafkaConsumer
import json
import asyncio
from kafka_config import KAFKA_BOOTSTRAP_SERVERS, OUT_TOPIC, CONSUMER_GROUP_PUBLISHER
from services.note_state_service import NoteStateService


class KafkaConsumerPublisher:
    def __init__(self):
        self.consumer = None
        self.note_state_service = NoteStateService()

    async def start(self):
        self.consumer = AIOKafkaConsumer(
            OUT_TOPIC,
            bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
            group_id=CONSUMER_GROUP_PUBLISHER,
            value_deserializer=lambda v: json.loads(v.decode('utf-8')),
            auto_offset_reset='earliest'
        )
        await self.consumer.start()
        asyncio.create_task(self.consume())

    async def stop(self):
        if self.consumer:
            await self.consumer.stop()

    async def consume(self):
        async for msg in self.consumer:
            # Обновляем статус заметки в памяти (если нужно)
            note_data = msg.value
            await self.note_state_service.update_note_state(
                note_data.get('issueId'),
                note_data.get('id'),
                note_data.get('state')
            )
kafka_consumer_publisher = KafkaConsumerPublisher()