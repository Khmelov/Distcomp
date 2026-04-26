import json
import threading
from kafka import KafkaConsumer, KafkaProducer
import app.services.comment_service as svc
from app.schemas.comment import CommentCreate, CommentUpdate

KAFKA_BOOTSTRAP = "localhost:9092"
IN_TOPIC = "InTopic"
OUT_TOPIC = "OutTopic"

STOP_WORDS = {"spam", "bad", "hate", "fuck", "abuse"}

producer = KafkaProducer(
    bootstrap_servers=KAFKA_BOOTSTRAP,
    value_serializer=lambda v: json.dumps(v).encode(),
)


def _moderate(content: str) -> str:
    words = set(content.lower().split())
    return "DECLINE" if words & STOP_WORDS else "APPROVE"


def _send_out(payload: dict):
    producer.send(OUT_TOPIC, value=payload)
    producer.flush()


def _handle(msg: dict):
    method = msg.get("method")
    cid = msg.get("correlationId")

    if method == "POST":
        # Нет ответа в OutTopic — fire-and-forget
        state = _moderate(msg["content"])
        data = CommentCreate(issueId=msg["issueId"], content=msg["content"])
        svc.create(data, comment_id=msg["id"], state=state)
        return  # не отвечаем

    elif method == "GET_ALL":
        results = svc.get_all()
        _send_out({"correlationId": cid, "data": [r.model_dump(by_alias=True) for r in results]})

    elif method == "GET":
        r = svc.get_by_id(msg["id"])
        if r:
            _send_out({"correlationId": cid, "data": r.model_dump(by_alias=True)})
        else:
            _send_out({"correlationId": cid, "error": {"errorMessage": f"Comment {msg['id']} not found", "errorCode": 40401}})

    elif method == "PUT":
        data = CommentUpdate(issueId=msg["issueId"], content=msg["content"])
        r = svc.update(msg["id"], data)
        if r:
            _send_out({"correlationId": cid, "data": r.model_dump(by_alias=True)})
        else:
            _send_out({"correlationId": cid, "error": {"errorMessage": f"Comment {msg['id']} not found", "errorCode": 40401}})

    elif method == "DELETE":
        found = svc.delete(msg["id"])
        if found:
            _send_out({"correlationId": cid, "data": {}})
        else:
            _send_out({"correlationId": cid, "error": {"errorMessage": f"Comment {msg['id']} not found", "errorCode": 40401}})


def start_kafka_worker():
    def _run():
        consumer = KafkaConsumer(
            IN_TOPIC,
            bootstrap_servers=KAFKA_BOOTSTRAP,
            value_deserializer=lambda m: json.loads(m.decode()),
            auto_offset_reset="earliest",
            group_id="discussion-group",
        )
        for msg in consumer:
            try:
                _handle(msg.value)
            except Exception as e:
                print(f"[kafka_worker] error: {e}")

    t = threading.Thread(target=_run, daemon=True)
    t.start()