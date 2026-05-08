import json
import os
import logging
import time

from kafka import KafkaProducer

logger = logging.getLogger(__name__)

KAFKA_BOOTSTRAP = os.environ.get("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
IN_TOPIC = "InTopic"

_producer = None


def get_producer():
    global _producer
    if _producer is None:
        for _ in range(30):
            try:
                _producer = KafkaProducer(
                    bootstrap_servers=KAFKA_BOOTSTRAP,
                    value_serializer=lambda v: json.dumps(v).encode("utf-8"),
                    key_serializer=lambda k: str(k).encode("utf-8") if k else None,
                )
                break
            except Exception:
                time.sleep(2)
    return _producer


def send_note_event(action: str, story_id: int, content: str = "",
                    country: str = "", note_id: int = None):
    producer = get_producer()
    if producer is None:
        logger.error("Kafka producer not available")
        return
    message = {
        "action": action,
        "storyId": story_id,
        "content": content,
        "country": country,
    }
    if note_id is not None:
        message["id"] = note_id
    producer.send(IN_TOPIC, key=str(story_id), value=message)
    producer.flush()
