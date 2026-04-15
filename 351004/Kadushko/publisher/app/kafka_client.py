import json
import uuid
import threading
from kafka import KafkaProducer, KafkaConsumer

KAFKA_BOOTSTRAP = "localhost:9092"
IN_TOPIC = "InTopic"
OUT_TOPIC = "OutTopic"

_pending: dict[str, dict] = {}
_lock = threading.Lock()

producer = KafkaProducer(
    bootstrap_servers=KAFKA_BOOTSTRAP,
    value_serializer=lambda v: json.dumps(v).encode(),
    key_serializer=lambda k: str(k).encode() if k else None,
)


def send_to_intopic(payload: dict, issue_id: int):
    """Отправить сообщение в InTopic. Partition по issue_id."""
    producer.send(IN_TOPIC, key=issue_id, value=payload)
    producer.flush()


def send_and_wait(payload: dict, issue_id: int, timeout: float = 1.0) -> dict | None:
    """Отправить запрос в InTopic, подождать ответ из OutTopic."""
    correlation_id = str(uuid.uuid4())
    payload["correlationId"] = correlation_id

    event = threading.Event()
    result_holder = {}

    with _lock:
        _pending[correlation_id] = {"event": event, "result": result_holder}

    producer.send(IN_TOPIC, key=issue_id, value=payload)
    producer.flush()

    triggered = event.wait(timeout=timeout)

    with _lock:
        _pending.pop(correlation_id, None)

    return result_holder.get("data") if triggered else None


def start_out_consumer():
    """Запустить consumer OutTopic в фоновом потоке."""
    def _consume():
        consumer = KafkaConsumer(
            OUT_TOPIC,
            bootstrap_servers=KAFKA_BOOTSTRAP,
            value_deserializer=lambda m: json.loads(m.decode()),
            auto_offset_reset="latest",
            group_id="publisher-group",
        )
        for msg in consumer:
            data = msg.value
            cid = data.get("correlationId")
            with _lock:
                entry = _pending.get(cid)
            if entry:
                entry["result"]["data"] = data
                entry["event"].set()

    t = threading.Thread(target=_consume, daemon=True)
    t.start()