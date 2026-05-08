import time
import json
import threading
import uvicorn
from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from kafka import KafkaConsumer, KafkaProducer
from schemas import PostRequestTo, PostResponseTo
from database import init_cassandra, get_session

app = FastAPI(title="Discussion Microservice")


class AppError(Exception):
    def __init__(self, status_code: int, error_code: int, message: str):
        self.status_code = status_code
        self.error_code = error_code
        self.message = message


@app.exception_handler(AppError)
async def app_exception_handler(request: Request, exc: AppError):
    return JSONResponse(
        status_code=exc.status_code,
        content={"errorMessage": exc.message, "errorCode": exc.error_code}
    )


# --- KAFKA PRODUCER ---
kafka_producer = None


def get_producer():
    global kafka_producer
    if kafka_producer is None:
        try:
            kafka_producer = KafkaProducer(
                bootstrap_servers=['localhost:9092'],
                value_serializer=lambda v: json.dumps(v).encode('utf-8'),
                linger_ms=0
            )
        except Exception:
            pass
    return kafka_producer


def notify_publisher(post_id: int, state: str):
    prod = get_producer()
    if prod:
        try:
            prod.send('OutTopic', value={"id": post_id, "state": state})
            prod.flush()
        except:
            pass


# --- KAFKA CONSUMER ---
def kafka_consumer_worker():
    consumer = None
    while True:
        try:
            consumer = KafkaConsumer(
                'InTopic',
                bootstrap_servers=['localhost:9092'],
                group_id='discussion-stable-group-v4',
                auto_offset_reset='earliest',
                value_deserializer=lambda x: json.loads(x.decode('utf-8'))
            )
            break
        except Exception:
            time.sleep(1)

    print("Discussion: Успешно подключен к Kafka (InTopic)!")

    for message in consumer:
        try:
            data = message.value
            post_id, article_id, content = data['id'], data['articleId'], data['content']
            country = data.get('country', 'BY')
            state = "DECLINE" if "spam" in content.lower() else "APPROVE"

            db = get_session()
            if db:
                db.execute(
                    "INSERT INTO tbl_post (id, article_id, content, country, state) VALUES (%s, %s, %s, %s, %s)",
                    (post_id, article_id, content, country, state)
                )
                notify_publisher(post_id, state)
        except Exception as e:
            print(f"Discussion Consumer Error: {e}")


@app.on_event("startup")
def startup_event():
    init_cassandra()
    threading.Thread(target=kafka_consumer_worker, daemon=True).start()

def get_post_with_retry(db, post_id: int, retries=50, delay=0.1):

    for _ in range(retries):
        row = db.execute("SELECT * FROM tbl_post WHERE id=%s", (post_id,)).one()
        if row: return row
        time.sleep(delay)
    return None


# --- REST ENDPOINTS ---
@app.post("/api/v1.0/posts", response_model=PostResponseTo, status_code=201)
def create_post(dto: PostRequestTo):
    db = get_session()
    post_id = int(time.time() * 1000)
    db.execute(
        "INSERT INTO tbl_post (id, article_id, content, country, state) VALUES (%s, %s, %s, %s, %s)",
        (post_id, dto.articleId, dto.content, getattr(dto, 'country', 'BY'), "PENDING")
    )
    return {"id": post_id, "articleId": dto.articleId, "content": dto.content, "state": "PENDING"}


@app.get("/api/v1.0/posts")
def get_posts():
    db = get_session()
    if not db: return []
    rows = db.execute("SELECT * FROM tbl_post")
    return [{"id": r["id"], "articleId": r["article_id"], "content": r["content"], "state": r.get("state", "PENDING")}
            for r in rows]


@app.get("/api/v1.0/posts/{id}")
def get_post(id: int):
    db = get_session()
    if not db: raise AppError(404, 40404, f"Post with id {id} not found")

    row = get_post_with_retry(db, id)
    if not row: raise AppError(404, 40404, f"Post with id {id} not found")

    return {"id": row["id"], "articleId": row["article_id"], "content": row["content"],
            "state": row.get("state", "PENDING")}


@app.put("/api/v1.0/posts/{id}")
def update_post(id: int, dto: PostRequestTo):
    db = get_session()
    if not db: raise AppError(404, 40404, f"Post with id {id} not found")

    row = get_post_with_retry(db, id)
    if not row: raise AppError(404, 40404, f"Post with id {id} not found")

    db.execute(
        "UPDATE tbl_post SET article_id=%s, content=%s, country=%s WHERE id=%s",
        (dto.articleId, dto.content, getattr(dto, 'country', 'BY'), id)
    )
    # ВАЖНО: Мы больше не шлем уведомление об инвалидации при "бэкдорном" REST PUT
    return {"id": id, "articleId": dto.articleId, "content": dto.content, "state": row.get("state", "PENDING")}


@app.delete("/api/v1.0/posts/{id}", status_code=204)
def delete_post(id: int):
    db = get_session()
    if not db: raise AppError(404, 40404, f"Post with id {id} not found")

    row = get_post_with_retry(db, id)
    if not row: raise AppError(404, 40404, f"Post with id {id} not found")

    db.execute("DELETE FROM tbl_post WHERE id=%s", (id,))


@app.delete("/api/v1.0/posts/by-article/{article_id}", status_code=204)
def delete_posts_by_article(article_id: int):
    db = get_session()
    if db:
        rows = db.execute("SELECT id FROM tbl_post WHERE article_id=%s", (article_id,))
        for row in rows:
            db.execute("DELETE FROM tbl_post WHERE id=%s", (row["id"],))


if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=24130, reload=True)