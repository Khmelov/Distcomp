"""
Discussion module Kafka worker.
Consumes from InTopic, processes CRUD operations on Cassandra, and sends responses to OutTopic.
"""
import json
import logging
import random
import threading

from kafka import KafkaConsumer, KafkaProducer
from kafka.errors import NoBrokersAvailable

logger = logging.getLogger(__name__)

STOP_WORDS = ["badword", "spam", "hate", "kill", "abuse"]


def _moderate(content: str) -> str:
    """Returns APPROVE or DECLINE based on content."""
    lower = content.lower()
    for word in STOP_WORDS:
        if word in lower:
            return "DECLINE"
    return "APPROVE"


def _handle(session, data: dict) -> dict:
    method = data.get("method")

    if method == "POST":
        comment_id = data.get("id") or random.randint(1, 2_000_000_000)
        content = data.get("content", "")
        topic_id = data.get("topicId")
        state = _moderate(content)
        session.execute(
            "INSERT INTO tbl_comment (id, topic_id, content, state) VALUES (%s, %s, %s, %s)",
            (comment_id, topic_id, content, state),
        )
        return {"status": 201, "payload": {
            "id": comment_id, "topicId": topic_id, "content": content, "state": state
        }}

    elif method == "GET_ALL":
        rows = session.execute(
            "SELECT id, topic_id, content, state FROM tbl_comment"
        )
        comments = [
            {"id": r["id"], "topicId": r["topic_id"],
             "content": r["content"], "state": r.get("state") or "APPROVE"}
            for r in rows
        ]
        return {"status": 200, "payload": comments}

    elif method == "GET":
        id_ = data.get("id")
        try:
            row = session.execute(
                "SELECT id, topic_id, content, state FROM tbl_comment WHERE id = %s",
                (id_,)
            ).one()
        except Exception:
            return {"status": 404, "error": "Comment not found"}
        if not row:
            return {"status": 404, "error": "Comment not found"}
        return {"status": 200, "payload": {
            "id": row["id"], "topicId": row["topic_id"],
            "content": row["content"], "state": row.get("state") or "APPROVE"
        }}

    elif method == "PUT":
        id_ = data.get("id")
        content = data.get("content", "")
        topic_id = data.get("topicId")
        try:
            existing = session.execute(
                "SELECT id FROM tbl_comment WHERE id = %s", (id_,)
            ).one()
        except Exception:
            return {"status": 404, "error": "Comment not found"}
        if not existing:
            return {"status": 404, "error": "Comment not found"}
        state = _moderate(content)
        session.execute(
            "UPDATE tbl_comment SET content = %s, topic_id = %s, state = %s WHERE id = %s",
            (content, topic_id, state, id_),
        )
        return {"status": 200, "payload": {
            "id": id_, "topicId": topic_id, "content": content, "state": state
        }}

    elif method == "DELETE":
        id_ = data.get("id")
        try:
            existing = session.execute(
                "SELECT id FROM tbl_comment WHERE id = %s", (id_,)
            ).one()
        except Exception:
            return {"status": 404, "error": "Comment not found"}
        if not existing:
            return {"status": 404, "error": "Comment not found"}
        session.execute("DELETE FROM tbl_comment WHERE id = %s", (id_,))
        return {"status": 204}

    else:
        return {"status": 400, "error": f"Unknown method: {method}"}


def _worker(bootstrap_servers: str, cassandra_session):
    try:
        consumer = KafkaConsumer(
            "InTopic",
            bootstrap_servers=bootstrap_servers,
            value_deserializer=lambda v: json.loads(v.decode("utf-8")),
            auto_offset_reset="earliest",
            group_id="discussion-in-consumer",
            enable_auto_commit=True,
            consumer_timeout_ms=-1,
        )
        producer = KafkaProducer(
            bootstrap_servers=bootstrap_servers,
            value_serializer=lambda v: json.dumps(v).encode("utf-8"),
            acks="all",
        )
        logger.info("Discussion Kafka worker started")

        for msg in consumer:
            data = msg.value
            method = data.get("method")
            request_id = data.get("requestId")

            try:
                result = _handle(cassandra_session, data)
            except Exception as e:
                logger.exception(f"Error handling message: {e}")
                result = {"status": 500, "error": str(e)}

            if method != "POST":
                response = {"requestId": request_id, **result}
                producer.send("OutTopic", value=response)
                producer.flush()

    except NoBrokersAvailable:
        logger.error("Kafka not available in discussion worker")
    except Exception as e:
        logger.exception(f"Discussion Kafka worker crashed: {e}")


def start_kafka_worker(bootstrap_servers: str, cassandra_session):
    """Start the background Kafka worker thread for discussion."""
    t = threading.Thread(
        target=_worker,
        args=(bootstrap_servers, cassandra_session),
        daemon=True,
        name="discussion-kafka-worker",
    )
    t.start()
    logger.info("Discussion Kafka worker thread launched")
