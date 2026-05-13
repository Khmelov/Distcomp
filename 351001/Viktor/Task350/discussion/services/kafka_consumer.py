from aiokafka import AIOKafkaConsumer
import json
from .post_service import PostService
from .kafka_producer import KafkaProducerService

class DiscussionKafkaConsumer:
    def __init__(self, post_service: PostService, producer: KafkaProducerService):
        self.post_service = post_service
        self.producer = producer
        self.consumer = AIOKafkaConsumer(
            'InTopic',
            bootstrap_servers='localhost:9092',
            group_id='discussion_group',
            value_deserializer=lambda m: json.loads(m.decode()),
            auto_offset_reset='earliest'
        )

    async def start(self):
        await self.consumer.start()
        async for msg in self.consumer:
            await self.process_message(msg.value)

    async def stop(self):
        await self.consumer.stop()

    async def process_message(self, msg):
        corr_id = msg['correlation_id']
        operation = msg['operation']
        data = msg['data']

        # 1. Выполняем операцию (с модерацией)
        try:
            if operation == 'create':
                result = await self.post_service.create_post(data['tweet_id'], data['content'])
            elif operation == 'update':
                result = await self.post_service.update_post(data['tweet_id'], data['post_id'], data['content'])
            # ... другие операции
            else:
                raise ValueError("Unknown operation")
        except Exception as e:
            result = {"error": str(e)}

        # 2. Отправляем ответ в OutTopic
        await self.producer.send_request("OutTopic", key=str(data.get("tweet_id", 0)), value={
            "correlation_id": corr_id,
            "result": result
        })