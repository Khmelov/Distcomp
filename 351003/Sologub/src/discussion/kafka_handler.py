import json
import os
import threading
import logging
import time

from kafka import KafkaConsumer, KafkaProducer

logger = logging.getLogger(__name__)

KAFKA_BOOTSTRAP = os.environ.get("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
IN_TOPIC = "InTopic"
OUT_TOPIC = "OutTopic"

STOP_WORDS = ["spam", "bad", "banned", "hate", "kill", "die"]


def moderate(content: str) -> str:
    lower = content.lower()
    for word in STOP_WORDS:
        if word in lower:
            return "DECLINE"
    return "APPROVE"


def _get_repository():
    from repository import CassandraNoteRepository
    host = os.environ.get("CASSANDRA_HOST", "localhost")
    port = int(os.environ.get("CASSANDRA_PORT", "9042"))
    return CassandraNoteRepository(host=host, port=port)


def _create_producer():
    for _ in range(30):
        try:
            return KafkaProducer(
                bootstrap_servers=KAFKA_BOOTSTRAP,
                value_serializer=lambda v: json.dumps(v).encode("utf-8"),
                key_serializer=lambda k: str(k).encode("utf-8") if k else None,
            )
        except Exception:
            time.sleep(2)
    raise RuntimeError("Cannot connect to Kafka")


def start_consumer():
    def _run():
        repo = None
        producer = None
        consumer = None
        while True:
            try:
                if consumer is None:
                    consumer = KafkaConsumer(
                        IN_TOPIC,
                        bootstrap_servers=KAFKA_BOOTSTRAP,
                        group_id="discussion-group",
                        value_deserializer=lambda m: json.loads(m.decode("utf-8")),
                        auto_offset_reset="earliest",
                        enable_auto_commit=True,
                    )
                if producer is None:
                    producer = _create_producer()
                if repo is None:
                    repo = _get_repository()
                break
            except Exception as e:
                logger.warning(f"Waiting for dependencies: {e}")
                time.sleep(3)

        logger.info("Discussion Kafka consumer started")
        for message in consumer:
            try:
                data = message.value
                action = data.get("action", "create")
                story_id = data.get("storyId")
                note_id = data.get("id")
                content = data.get("content", "")
                country = data.get("country", "")

                if action == "create":
                    state = moderate(content)
                    note = repo.create(story_id=story_id, content=content,
                                       country=country, state=state, note_id=note_id)
                    result = {
                        "id": note.id,
                        "storyId": note.storyId,
                        "content": note.content,
                        "country": note.country,
                        "state": note.state,
                        "action": "create",
                    }
                elif action == "update":
                    state = moderate(content)
                    note = repo.update(story_id=story_id, note_id=note_id, content=content, country=country, state=state)
                    if not note:
                        # Upsert: reconcile Cassandra with REST state when the
                        # original create message was lost or arrived out of order.
                        note = repo.create(story_id=story_id, content=content,
                                           country=country, state=state, note_id=note_id)
                    result = {
                        "id": note.id,
                        "storyId": note.storyId,
                        "content": note.content,
                        "country": note.country,
                        "state": note.state,
                        "action": "update",
                    }
                elif action == "delete":
                    success = repo.delete(story_id=story_id, note_id=note_id)
                    result = {
                        "id": note_id,
                        "storyId": story_id,
                        "action": "delete",
                        "success": success,
                    }
                else:
                    continue

                producer.send(OUT_TOPIC, key=str(story_id), value=result)
                producer.flush()
            except Exception as e:
                logger.error(f"Error processing message: {e}")

    thread = threading.Thread(target=_run, daemon=True)
    thread.start()
