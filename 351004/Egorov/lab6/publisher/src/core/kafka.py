from aiokafka import AIOKafkaProducer

from .settings import settings

kafka_producer: AIOKafkaProducer | None = None

async def get_kafka_producer() -> AIOKafkaProducer:
    return kafka_producer

async def init_kafka_producer() -> None:
    global kafka_producer
    kafka_producer = AIOKafkaProducer(
        bootstrap_servers=settings.kafka.bootstrap_servers
    )

    await kafka_producer.start()

async def close_kafka_producer() -> None:
    global kafka_producer
    if kafka_producer:
        await kafka_producer.stop()