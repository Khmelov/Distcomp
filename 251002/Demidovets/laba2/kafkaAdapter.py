import time
from typing import Optional

from pydantic import BaseModel
from aiokafka import AIOKafkaConsumer, AIOKafkaProducer
from loguru import logger
import asyncio
from models import Comment


import api_v1.comments.helper as mes
import api_v1.comments.helper as mes
import api_v1.comments.views as mes1

logger.add(
    sink="RV1Lab.log",
    mode="w",
    encoding="utf-8",
    format="{level} {comment}",
)

import json

def comment_to_json(comment: Comment) -> str:
    comment_dict = {
        "id": comment.id,
        "newsId": comment.newsId,
        "content": comment.content,
    }
    return json.dumps(comment_dict)

def comment_to_json_none() -> str:
    comment_dict = {
        "id": 0,
        "newsId": 0,
        "content": "",
    }
    return json.dumps(comment_dict)

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
            comment = await mes.fetch_comment(int(value))
            logger.info(comment)

            # logger.info(comment.id)
            if comment is None:
                logger.info( comment_to_json_none())
            else:
                logger.info( comment_to_json(comment=comment))
           




            # comment1 = await mes1.comment_by_id(int(value))
            # logger.info(comment1)
            
            try:
                # await producer.send(OUT_TOPIC, value=comment_to_json(comment=comment, id=value).encode('utf-8'))
                if comment is None:
                    await producer.send(OUT_TOPIC, value=comment_to_json_none(), partition=0)
                    logger.info(f"Отправлено в {OUT_TOPIC}: {comment_to_json_none()}")
                else:
                    await producer.send(OUT_TOPIC, value=comment_to_json(comment=comment), partition=0)
                    logger.info(f"Отправлено в {OUT_TOPIC}: {comment_to_json(comment=comment)}")
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