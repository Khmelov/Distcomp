import asyncio
import uuid
import json
from aiokafka import AIOKafkaProducer, AIOKafkaConsumer

KAFKA_BOOTSTRAP_SERVERS = "broker:29092"
INTOPIC = "InTopic"
OUTTOPIC = "OutTopic"

pending_requests: dict[str, asyncio.Future] = {}

producer: AIOKafkaProducer = None


async def init_producer():
    global producer
    if producer is None:
        producer = AIOKafkaProducer(
            bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
            value_serializer=lambda v: json.dumps(v).encode("utf-8"),
            key_serializer=lambda k: str(k).encode("utf-8"),
        )
        await producer.start()
    return producer


async def send_kafka_request(method: str, payload: dict) -> dict:
    correlation_id = str(uuid.uuid4())
    message = {
        "correlation_id": correlation_id,
        "method": method,
        "payload": payload,
    }
    fut = asyncio.get_running_loop().create_future()
    pending_requests[correlation_id] = fut

    prod = await init_producer()
    key = payload.get("storyId", correlation_id)
    await prod.send(INTOPIC, value=message, key=key)
    result = await asyncio.wait_for(fut, timeout=10)
    await pending_requests.pop(correlation_id, None)
    return result


async def kafka_response_consumer():
    consumer = AIOKafkaConsumer(
        OUTTOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        value_deserializer=lambda v: json.loads(v.decode("utf-8")),
        key_deserializer=lambda k: k.decode("utf-8") if k else None,
        group_id="publisher_group",
    )
    await consumer.start()
    try:
        async for msg in consumer:
            response = msg.value
            corr_id = response.get("correlation_id")
            result = response.get("result")
            if corr_id in pending_requests:
                pending_requests[corr_id].set_result(result)
    finally:
        await consumer.stop()
