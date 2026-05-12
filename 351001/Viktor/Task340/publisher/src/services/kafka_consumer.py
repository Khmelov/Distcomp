from aiokafka import AIOKafkaConsumer
import json

class KafkaConsumerService:
    def __init__(self, bootstrap_servers='127.0.0.1:9092', group_id='publisher_group'):
        self.consumer = AIOKafkaConsumer(
            'OutTopic',
            bootstrap_servers=bootstrap_servers,
            group_id=group_id,
            value_deserializer=lambda m: json.loads(m.decode()),
            auto_offset_reset='earliest'
        )

    async def start(self):
        await self.consumer.start()

    async def stop(self):
        await self.consumer.stop()

    async def get_responses(self, correlation_id_map):
        # Читаем сообщения и сопоставляем correlation_id
        async for msg in self.consumer:
            data = msg.value
            corr_id = data.get('correlation_id')
            if corr_id in correlation_id_map:
                correlation_id_map[corr_id].set_result(data)