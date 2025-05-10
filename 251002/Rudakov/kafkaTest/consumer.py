from iokafka import AIOKafkaConsumer
import asyncio
import json

async def consume():
    consumer = AIOKafkaConsumer(
        "InTopic",
        bootstrap_servers='kafka:9092',
        group_id="my-group",
        value_deserializer=lambda v: json.loads(v.decode('utf-8'))
    )
    
    await consumer.start()
    
    try:
        async for message in consumer:
            print(f"Consumed: {message.value}")
    finally:
        await consumer.stop()

if __name__ == "__main__":
    asyncio.run(consume())