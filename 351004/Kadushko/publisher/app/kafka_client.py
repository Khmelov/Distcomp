import json
import uuid
import threading
import asyncio
from aiokafka import AIOKafkaProducer, AIOKafkaConsumer

KAFKA_BOOTSTRAP = "localhost:9092"
IN_TOPIC = "InTopic"
OUT_TOPIC = "OutTopic"

_pending: dict[str, dict] = {}
_lock = threading.Lock()
_loop = None
_producer = None


async def _start_producer():
    global _producer
    _producer = AIOKafkaProducer(
        bootstrap_servers=KAFKA_BOOTSTRAP,
        value_serializer=lambda v: json.dumps(v).encode(),
        key_serializer=lambda k: str(k).encode() if k else None,
    )
    await _producer.start()


async def _send(topic, key, value):
    await _producer.send_and_wait(topic, key=key, value=value)


def _get_loop():
    global _loop
    return _loop


def send_to_intopic(payload: dict, issue_id: int):
    asyncio.run_coroutine_threadsafe(_send(IN_TOPIC, issue_id, payload), _get_loop()).result()


def send_and_wait(payload: dict, issue_id: int, timeout: float = 1.5) -> dict | None:
    correlation_id = str(uuid.uuid4())
    payload["correlationId"] = correlation_id

    event = threading.Event()
    result_holder = {}

    with _lock:
        _pending[correlation_id] = {"event": event, "result": result_holder}

    asyncio.run_coroutine_threadsafe(_send(IN_TOPIC, issue_id, payload), _get_loop()).result()

    triggered = event.wait(timeout=timeout)

    with _lock:
        _pending.pop(correlation_id, None)

    return result_holder.get("data") if triggered else None


async def _consume_out():
    consumer = AIOKafkaConsumer(
        OUT_TOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP,
        value_deserializer=lambda m: json.loads(m.decode()),
        auto_offset_reset="latest",
        group_id="publisher-group",
    )
    await consumer.start()
    print("[publisher] OutTopic consumer ready")
    async for msg in consumer:
        data = msg.value
        cid = data.get("correlationId")
        with _lock:
            entry = _pending.get(cid)
        if entry:
            entry["result"]["data"] = data
            entry["event"].set()


def start_out_consumer():
    global _loop

    def _thread():
        global _loop
        _loop = asyncio.new_event_loop()
        asyncio.set_event_loop(_loop)
        _loop.run_until_complete(_start_producer())
        _loop.run_until_complete(_consume_out())

    t = threading.Thread(target=_thread, daemon=True)
    t.start()
    # Подождать пока loop запустится
    import time
    time.sleep(2)