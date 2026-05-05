import json
import asyncio
import threading
from aiokafka import AIOKafkaConsumer, AIOKafkaProducer
import app.services.comment_service as svc
from app.schemas.comment import CommentCreate, CommentUpdate

KAFKA_BOOTSTRAP = "localhost:9092"
IN_TOPIC = "InTopic"
OUT_TOPIC = "OutTopic"

STOP_WORDS = {"spam", "bad", "hate", "fuck", "abuse"}


def _moderate(content: str) -> str:
    words = set(content.lower().split())
    return "DECLINE" if words & STOP_WORDS else "APPROVE"


async def _run():
    producer = AIOKafkaProducer(
        bootstrap_servers=KAFKA_BOOTSTRAP,
        value_serializer=lambda v: json.dumps(v).encode(),
    )
    consumer = AIOKafkaConsumer(
        IN_TOPIC,
        bootstrap_servers=KAFKA_BOOTSTRAP,
        value_deserializer=lambda m: json.loads(m.decode()),
        auto_offset_reset="earliest",
        group_id="discussion-group",
    )
    await producer.start()
    await consumer.start()
    print("[discussion] kafka consumer ready")

    async def send_out(payload: dict):
        await producer.send_and_wait(OUT_TOPIC, value=payload)

    try:
        async for msg in consumer:
            data = msg.value
            print(f"[discussion] received: {data}")
            method = data.get("method")
            cid = data.get("correlationId")
            try:
                if method == "POST":
                    state = _moderate(data["content"])
                    svc.create(CommentCreate(issueId=data["issueId"], content=data["content"]), comment_id=data["id"], state=state)

                elif method == "GET_ALL":
                    results = svc.get_all()
                    await send_out({"correlationId": cid, "data": [r.model_dump(by_alias=True) for r in results]})

                elif method == "GET":
                    r = svc.get_by_id(data["id"])
                    if r:
                        await send_out({"correlationId": cid, "data": r.model_dump(by_alias=True)})
                    else:
                        await send_out({"correlationId": cid, "error": {"errorMessage": f"Comment {data['id']} not found", "errorCode": 40401}})

                elif method == "PUT":
                    r = svc.update(data["id"], CommentUpdate(issueId=data["issueId"], content=data["content"]))
                    if r:
                        await send_out({"correlationId": cid, "data": r.model_dump(by_alias=True)})
                    else:
                        await send_out({"correlationId": cid, "error": {"errorMessage": f"Comment {data['id']} not found", "errorCode": 40401}})

                elif method == "DELETE":
                    found = svc.delete(data["id"])
                    if found:
                        await send_out({"correlationId": cid, "data": {}})
                    else:
                        await send_out({"correlationId": cid, "error": {"errorMessage": f"Comment {data['id']} not found", "errorCode": 40401}})
            except Exception as e:
                print(f"[kafka_worker] error: {e}")
    finally:
        await consumer.stop()
        await producer.stop()


def start_kafka_worker():
    def _thread():
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        loop.run_until_complete(_run())

    t = threading.Thread(target=_thread, daemon=True)
    t.start()
    print("[discussion] kafka thread launched")