from iokafka import AIOKafkaProducer
import asyncio
import json

async def produce():
    producer = AIOKafkaProducer(
        bootstrap_servers='kafka:9092',
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )
    
    await producer.start()
    
    try:
        for i in range(100):
            message = {"message": f"Hello Kafka #{i}", "number": i}
            await producer.send("InTopic", value=message)
            print(f"Produced: {message}")
            await asyncio.sleep(1)
    finally:
        await producer.stop()

if __name__ == "__main__":
    asyncio.run(produce())