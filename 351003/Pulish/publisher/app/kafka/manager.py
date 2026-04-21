import json
import uuid
import threading
import logging
from kafka import KafkaProducer, KafkaConsumer
from kafka.errors import NoBrokersAvailable

logger = logging.getLogger(__name__)

_pending: dict[str, dict] = {}
_lock = threading.Lock()

_producer: KafkaProducer | None = None
_initialized = False


def init_kafka(bootstrap_servers: str):
    """Initialize Kafka producer and start OutTopic consumer thread."""
    global _producer, _initialized
    if _initialized:
        return
    try:
        _producer = KafkaProducer(
            bootstrap_servers=bootstrap_servers,
            value_serializer=lambda v: json.dumps(v).encode("utf-8"),
            key_serializer=lambda k: str(k).encode(
                "utf-8") if k is not None else None,
            acks="all",
            retries=3,
        )
        t = threading.Thread(
            target=_out_topic_consumer,
            args=(bootstrap_servers,),
            daemon=True,
            name="kafka-out-consumer",
        )
        t.start()
        _initialized = True
        logger.info("Kafka initialized, OutTopic consumer started")
    except NoBrokersAvailable:
        logger.warning("Kafka not available — comment operations will fail")


def _out_topic_consumer(bootstrap_servers: str):
    """Background thread: reads OutTopic and resolves pending request-reply futures."""
    try:
        consumer = KafkaConsumer(
            "OutTopic",
            bootstrap_servers=bootstrap_servers,
            value_deserializer=lambda v: json.loads(v.decode("utf-8")),
            auto_offset_reset="latest",
            group_id="publisher-out-consumer",
            enable_auto_commit=True,
            consumer_timeout_ms=-1,
        )
        for msg in consumer:
            data = msg.value
            request_id = data.get("requestId")
            if not request_id:
                continue
            with _lock:
                entry = _pending.get(request_id)
            if entry:
                entry["result"] = data
                entry["event"].set()
    except Exception as e:
        logger.error(f"OutTopic consumer error: {e}")


def send_fire_and_forget(method: str, payload: dict, key=None):
    """Send message to InTopic without waiting for reply (used for POST)."""
    if _producer is None:
        raise RuntimeError("Kafka not initialized")
    request_id = str(uuid.uuid4())
    message = {"requestId": request_id, "method": method, **payload}
    _producer.send("InTopic", value=message, key=key)
    _producer.flush()


def send_and_wait(method: str, payload: dict, key=None, timeout: float = 1.0) -> dict | None:
    """
    Send message to InTopic and wait up to `timeout` seconds for a reply on OutTopic.
    Returns the response dict or None on timeout.
    """
    if _producer is None:
        raise RuntimeError("Kafka not initialized")
    request_id = str(uuid.uuid4())
    message = {"requestId": request_id, "method": method, **payload}

    event = threading.Event()
    with _lock:
        _pending[request_id] = {"event": event, "result": None}

    _producer.send("InTopic", value=message, key=key)
    _producer.flush()

    got = event.wait(timeout=timeout)

    with _lock:
        entry = _pending.pop(request_id, {})

    if not got:
        logger.warning(
            f"Timeout waiting for response to requestId={request_id}")
        return None
    return entry.get("result")
