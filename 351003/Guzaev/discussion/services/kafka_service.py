import json
import threading

STOP_WORDS = ["spam", "ban", "bad", "hate"]

def moderate(content: str) -> str:
    for word in STOP_WORDS:
        if word.lower() in content.lower():
            return "DECLINE"
    return "APPROVE"

def start_consumer(repo):
    from kafka import KafkaConsumer, KafkaProducer
    print("Kafka consumer connecting...")
    consumer = KafkaConsumer(
        "InTopic",
        bootstrap_servers="localhost:9092",
        value_deserializer=lambda m: json.loads(m.decode("utf-8")),
        group_id="discussion-group",
        auto_offset_reset="earliest"
    )
    producer = KafkaProducer(
        bootstrap_servers="localhost:9092",
        value_serializer=lambda v: json.dumps(v).encode("utf-8"),
        key_serializer=lambda k: str(k).encode("utf-8") if k else None
    )
    print("Kafka consumer started, waiting for messages...")
    for msg in consumer:
        print(f"Discussion received: {msg.value}")
        data = msg.value
        action = data.get("action")
        try:
            result = handle_action(action, data, repo)
        except Exception as e:
            result = {"errorMessage": str(e), "errorCode": 50000}
        producer.send("OutTopic", key=data.get("request_id"), value=result)
        producer.flush()

def handle_action(action, data, repo):
    from models.comment import Comment
    if action == "CREATE":
        c = Comment(
            id=data["id"], tweet_id=data["tweetId"],
            content=data["content"], country=data.get("country", "Belarus"),
            state=moderate(data["content"])
        )
        saved = repo.create(c)
        return {"id": saved.id, "tweetId": saved.tweet_id,
                "content": saved.content, "country": saved.country, "state": saved.state}
    elif action == "GET_ALL":
        return [{"id": c.id, "tweetId": c.tweet_id, "content": c.content,
                 "country": c.country, "state": c.state} for c in repo.get_all()]
    elif action == "GET":
        c = repo.get_by_id(data["id"])
        if not c:
            return {"errorMessage": "Not found", "errorCode": 40400}
        return {"id": c.id, "tweetId": c.tweet_id, "content": c.content,
                "country": c.country, "state": c.state}
    elif action == "UPDATE":
        c = Comment(id=data["id"], tweet_id=data["tweetId"],
                    content=data["content"], country=data.get("country", "Belarus"),
                    state=moderate(data["content"]))
        updated = repo.update(c)
        return {"id": updated.id, "tweetId": updated.tweet_id,
                "content": updated.content, "country": updated.country, "state": updated.state}
    elif action == "DELETE":
        repo.delete(data["id"])
        return {"deleted": True}
    return {"errorMessage": "Unknown action", "errorCode": 40000}

def start_kafka_thread(repo):
    print("Starting Kafka thread...")
    t = threading.Thread(target=start_consumer, args=(repo,), daemon=True)
    t.start()