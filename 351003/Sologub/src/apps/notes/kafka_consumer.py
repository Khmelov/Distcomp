import json
import os
import threading
import logging
import time

from kafka import KafkaConsumer

logger = logging.getLogger(__name__)

KAFKA_BOOTSTRAP = os.environ.get("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
OUT_TOPIC = "OutTopic"


def start_consumer():
    def _run():
        consumer = None
        while True:
            try:
                consumer = KafkaConsumer(
                    OUT_TOPIC,
                    bootstrap_servers=KAFKA_BOOTSTRAP,
                    group_id="publisher-group",
                    value_deserializer=lambda m: json.loads(m.decode("utf-8")),
                    auto_offset_reset="earliest",
                    enable_auto_commit=True,
                )
                break
            except Exception as e:
                logger.warning(f"Waiting for Kafka: {e}")
                time.sleep(3)

        logger.info("Publisher Kafka consumer started (OutTopic)")
        for message in consumer:
            try:
                data = message.value
                action = data.get("action")
                note_id = data.get("id")
                state = data.get("state", "")
                logger.info(f"OutTopic: action={action} id={note_id} state={state}")
            except Exception as e:
                logger.error(f"Error processing OutTopic message: {e}")

    thread = threading.Thread(target=_run, daemon=True)
    thread.start()
