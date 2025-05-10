import time
from typing import Optional

from pydantic import BaseModel
from aiokafka import AIOKafkaConsumer, AIOKafkaProducer
from loguru import logger
import asyncio
from models import Message


import api_v1.messages.helper as mes
import api_v1.messages.helper as mes
import api_v1.messages.views as mes1

logger.add(
    sink="RV1Lab.log",
    mode="w",
    encoding="utf-8",
    format="{level} {message}",
)

import json

def message_to_json(message: Message) -> str:
    message_dict = {
        "id": message.id,
        "storyId": message.storyId,
        "content": message.content,
    }
    return json.dumps(message_dict)

def message_to_json_none() -> str:
    message_dict = {
        "id": 0,
        "storyId": 0,
        "content": "",
    }
    return json.dumps(message_dict)

# Для подключения из другого контейнера
BROKERS = "broker:29092"

# Для подключения с хоста (не из Docker)
# BROKERS = "0.0.0.0:9092" 

GROUP_ID = "test-group"
IN_TOPIC = "InTopic"
OUT_TOPIC = "OutTopic"

async def consume_and_produce():
    consumer = None
    producer = None
    
    try:
        # Настройка consumer с увеличенными таймаутами
        consumer = AIOKafkaConsumer(
            IN_TOPIC,

            bootstrap_servers=BROKERS,
            group_id=GROUP_ID,
            auto_offset_reset="earliest",
            value_deserializer=lambda v: int(v.decode('utf-8')),
            request_timeout_ms=30000,
            session_timeout_ms=25000
        )



        
        # Настройка producer
        producer = AIOKafkaProducer(
            bootstrap_servers=BROKERS,
            value_serializer=lambda v: str(v).encode('utf-8'),
            request_timeout_ms=30000
            
        )


        await producer.start()
        await consumer.start()
        logger.info(f"Успешное подключение к Kafka {BROKERS}")


        async for msg in consumer:
            value = msg.value
            logger.info(f"Получено из {IN_TOPIC}: {value}")
            # time.sleep(1000) 
            message = await mes.fetch_message(int(value))
            logger.info(message)

            # logger.info(message.id)
            if message is None:
                logger.info( message_to_json_none())
            else:
                logger.info( message_to_json(message=message))
           




            # message1 = await mes1.message_by_id(int(value))
            # logger.info(message1)
            
            try:
                # await producer.send(OUT_TOPIC, value=message_to_json(message=message, id=value).encode('utf-8'))
                if message is None:
                    await producer.send(OUT_TOPIC, value=message_to_json_none(), partition=0)
                    logger.info(f"Отправлено в {OUT_TOPIC}: {message_to_json_none()}")
                else:
                    await producer.send(OUT_TOPIC, value=message_to_json(message=message), partition=0)
                    logger.info(f"Отправлено в {OUT_TOPIC}: {message_to_json(message=message)}")
            except Exception as e:
                logger.error(f"Ошибка отправки: {e}")




    except Exception as e:
        logger.error(f"Ошибка в работе с Kafka: {e}")
    finally:
        if consumer:
            await consumer.stop()
        if producer:
            await producer.stop()
        logger.info("Соединения закрыты")
# if __name__ == "__main__":
#     asyncio.run(consume_and_produce())