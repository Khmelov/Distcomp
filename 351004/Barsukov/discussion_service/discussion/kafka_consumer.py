from aiokafka import AIOKafkaConsumer
import json
import asyncio
from discussion.kafka_config import KAFKA_BOOTSTRAP_SERVERS, IN_TOPIC, CONSUMER_GROUP_DISCUSSION
from discussion.services.note_service import NoteService
from discussion.db.thecassandra import cassandra_client
from discussion.kafka_producer import kafka_producer


class KafkaConsumerDiscussion:
    def __init__(self):
        self.consumer = None

    async def start(self):
        self.consumer = AIOKafkaConsumer(
            IN_TOPIC,
            bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
            group_id=CONSUMER_GROUP_DISCUSSION,
            value_deserializer=lambda v: json.loads(v.decode('utf-8')),
            auto_offset_reset='earliest'
        )
        await self.consumer.start()
        asyncio.create_task(self.consume())

    async def stop(self):
        if self.consumer:
            await self.consumer.stop()

    async def moderate_note(self, content: str) -> str:
        """Простая модерация на основе стоп-слов"""
        stop_words = ["spam", "badword", "offensive", "плохое", "спам"]
        content_lower = content.lower()
        for word in stop_words:
            if word in content_lower:
                return "DECLINE"
        return "APPROVE"

    async def consume(self):
        async for msg in self.consumer:
            note_data = msg.value
            # Модерируем заметку
            state = await self.moderate_note(note_data.get('content', ''))

            # Обновляем статус в Cassandra
            session = cassandra_client.session
            service = NoteService(session)

            try:
                updated_note = service.update_state(
                    note_data.get('issueId'),
                    note_data.get('id'),
                    state
                )

                # Отправляем результат в OutTopic
                await kafka_producer.send_message(
                    "OutTopic",
                    key=str(updated_note.issueId),
                    value=updated_note.model_dump()
                )
            except Exception as e:
                print(f"Error processing note: {e}")