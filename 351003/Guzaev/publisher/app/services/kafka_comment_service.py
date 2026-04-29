import json
import uuid
import threading
import time
from kafka import KafkaProducer, KafkaConsumer

_responses: dict = {}
_consumer_ready = threading.Event()  # флаг готовности

def _start_out_consumer():
    consumer = KafkaConsumer(
        "OutTopic",
        bootstrap_servers="localhost:9092",
        value_deserializer=lambda m: json.loads(m.decode("utf-8")),
        key_deserializer=lambda k: k.decode("utf-8") if k else None,
        group_id="publisher-group",
        auto_offset_reset="latest",
        enable_auto_commit=True
    )
    print("Publisher OutTopic consumer started")
    _consumer_ready.set()  # сигнал — consumer готов
    for msg in consumer:
        req_id = msg.key
        print(f"Got response: {req_id}")
        if req_id:
            _responses[req_id] = msg.value

def start_consumer_thread():
    t = threading.Thread(target=_start_out_consumer, daemon=True)
    t.start()
    # Жди пока consumer реально подключится
    _consumer_ready.wait(timeout=10)
    print("Consumer ready, publisher can send messages")

_producer = None

def get_producer():
    global _producer
    if _producer is None:
        _producer = KafkaProducer(
            bootstrap_servers="localhost:9092",
            value_serializer=lambda v: json.dumps(v).encode("utf-8"),
            key_serializer=lambda k: str(k).encode("utf-8") if k else None
        )
    return _producer

def send_and_wait(action: str, data: dict, tweet_id: int = None, timeout: float = 5.0):
    req_id = str(uuid.uuid4())
    data["action"] = action
    data["request_id"] = req_id
    key = str(tweet_id) if tweet_id else None
    get_producer().send("InTopic", key=key, value=data)
    get_producer().flush()
    print(f"Sent {action}, waiting for {req_id}")

    deadline = time.time() + timeout
    while time.time() < deadline:
        if req_id in _responses:
            return _responses.pop(req_id)
        time.sleep(0.05)
    print(f"TIMEOUT for {req_id}")
    return {"errorMessage": "Timeout waiting for discussion", "errorCode": 50800}
