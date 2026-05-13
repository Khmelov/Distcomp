import os
import time
import json
import asyncio
from contextlib import asynccontextmanager

from fastapi import FastAPI, Request, HTTPException, Response
from cassandra.cluster import Cluster
from aiokafka import AIOKafkaConsumer, AIOKafkaProducer

from src.schemas import PostRequestTo, PostResponseTo

KAFKA_BOOTSTRAP_SERVERS = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")


def moderate_post(content: str) -> str:
    stop_words = ["badword", "spam", "реклама"]
    content_lower = content.lower()
    for word in stop_words:
        if word in content_lower:
            return "DECLINE"
    return "APPROVE"


async def kafka_background_task(app: FastAPI):
    consumer = AIOKafkaConsumer(
        "InTopic",
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        group_id="discussion_group",
        auto_offset_reset='earliest'
    )
    producer = AIOKafkaProducer(bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS)

    retries = 10
    for i in range(retries):
        try:
            await consumer.start()
            await producer.start()
            print("Discussion: Successfully connected to Kafka!")
            break
        except Exception as e:
            print(f"Waiting for Kafka to be ready... ({i + 1}/{retries})")
            await asyncio.sleep(5)
    else:
        print("Discussion: Failed to connect to Kafka.")
        return

    session = app.state.cassandra_session

    try:
        async for msg in consumer:
            try:
                data = json.loads(msg.value.decode('utf-8'))
                action = data.get("action")
                req_id = data.get("request_id")

                response_data = None
                error_data = None

                if action == "CREATE":
                    post_data = data["data"]
                    post_id = post_data["id"]
                    issue_id = post_data["issue_id"]
                    content = post_data["content"]

                    state = moderate_post(content)
                    query = "INSERT INTO tbl_post (issue_id, id, country, content, state) VALUES (%s, %s, %s, %s, %s)"
                    session.execute(query, (issue_id, post_id, "RU", content, state))
                    continue

                elif action == "GET_ALL":
                    issue_id = data.get("data", {}).get("issue_id")
                    if issue_id:
                        rows = session.execute("SELECT id, issue_id, content, state FROM tbl_post WHERE issue_id=%s",
                                               (issue_id,))
                    else:
                        rows = session.execute("SELECT id, issue_id, content, state FROM tbl_post")
                    response_data = [{"id": r.id, "issueId": r.issue_id, "content": r.content, "state": r.state} for r
                                     in rows]

                elif action == "GET_ONE":
                    post_id = data.get("data", {}).get("id")
                    row = session.execute(
                        "SELECT id, issue_id, content, state FROM tbl_post WHERE id=%s ALLOW FILTERING",
                        (post_id,)).one()
                    if row:
                        response_data = {"id": row.id, "issueId": row.issue_id, "content": row.content,
                                         "state": row.state}
                    else:
                        error_data = {"status_code": 404, "detail": "Post not found"}

                elif action == "UPDATE":
                    post_id = data.get("data", {}).get("id")
                    content = data["data"]["content"]
                    row = session.execute("SELECT issue_id FROM tbl_post WHERE id=%s ALLOW FILTERING", (post_id,)).one()
                    if row:
                        new_state = moderate_post(content)
                        session.execute("UPDATE tbl_post SET content=%s, state=%s WHERE issue_id=%s AND id=%s",
                                        (content, new_state, row.issue_id, post_id))
                        response_data = {"id": post_id, "issueId": row.issue_id, "content": content, "state": new_state}
                    else:
                        error_data = {"status_code": 404, "detail": "Post not found"}

                elif action == "DELETE":
                    post_id = data.get("data", {}).get("id")
                    row = session.execute("SELECT issue_id FROM tbl_post WHERE id=%s ALLOW FILTERING", (post_id,)).one()
                    if row:
                        session.execute("DELETE FROM tbl_post WHERE issue_id=%s AND id=%s", (row.issue_id, post_id))
                        response_data = {"success": True}
                    else:
                        error_data = {"status_code": 404, "detail": "Post not found"}

                if req_id:
                    out_msg = json.dumps({"request_id": req_id, "result": response_data, "error": error_data}).encode(
                        'utf-8')
                    await producer.send_and_wait("OutTopic", out_msg)

            except Exception as e:
                print(f"Error processing kafka message: {e}")

    finally:
        await consumer.stop()
        await producer.stop()


@asynccontextmanager
async def lifespan(app: FastAPI):
    host = os.getenv("CASSANDRA_HOST", "localhost")
    cluster = Cluster([host], port=9042)
    session = cluster.connect()

    session.execute("""
        CREATE KEYSPACE IF NOT EXISTS distcomp 
        WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
    """)
    session.set_keyspace("distcomp")

    session.execute("""
                    CREATE TABLE IF NOT EXISTS tbl_post
                    (
                        issue_id
                        bigint,
                        id
                        bigint,
                        country
                        text,
                        content
                        text,
                        state
                        text,
                        PRIMARY
                        KEY (
                    (
                        issue_id
                    ), id)
                        ) WITH CLUSTERING ORDER BY (id ASC);
                    """)

    app.state.cassandra_session = session
    task = asyncio.create_task(kafka_background_task(app))
    yield
    task.cancel()
    cluster.shutdown()


app = FastAPI(lifespan=lifespan, title="Discussion Service")


@app.post("/api/v1.0/posts", response_model=PostResponseTo, status_code=201)
def create_post(post_in: PostRequestTo, request: Request):
    session = request.app.state.cassandra_session
    post_id = int(time.time() * 1000)
    country = "RU"
    state = moderate_post(post_in.content)
    query = "INSERT INTO tbl_post (issue_id, id, country, content, state) VALUES (%s, %s, %s, %s, %s)"
    session.execute(query, (post_in.issue_id, post_id, country, post_in.content, state))
    return PostResponseTo(id=post_id, content=post_in.content, issue_id=post_in.issue_id, state=state)


@app.get("/api/v1.0/posts", response_model=list[PostResponseTo])
def get_posts(request: Request, issueId: int = None):
    session = request.app.state.cassandra_session
    if issueId:
        rows = session.execute("SELECT id, issue_id, content, state FROM tbl_post WHERE issue_id=%s", (issueId,))
    else:
        rows = session.execute("SELECT id, issue_id, content, state FROM tbl_post")
    return [PostResponseTo(id=row.id, issue_id=row.issue_id, content=row.content, state=row.state or "PENDING") for row
            in rows]


@app.get("/api/v1.0/posts/{id}", response_model=PostResponseTo)
def get_post(id: int, request: Request):
    session = request.app.state.cassandra_session
    row = session.execute("SELECT id, issue_id, content, state FROM tbl_post WHERE id=%s ALLOW FILTERING", (id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Post not found")
    return PostResponseTo(id=row.id, issue_id=row.issue_id, content=row.content, state=row.state or "PENDING")


@app.put("/api/v1.0/posts/{id}", response_model=PostResponseTo)
def update_post(id: int, post_in: PostRequestTo, request: Request):
    session = request.app.state.cassandra_session
    row = session.execute("SELECT issue_id FROM tbl_post WHERE id=%s ALLOW FILTERING", (id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Post not found")
    new_state = moderate_post(post_in.content)
    session.execute("UPDATE tbl_post SET content=%s, state=%s WHERE issue_id=%s AND id=%s",
                    (post_in.content, new_state, row.issue_id, id))
    return PostResponseTo(id=id, issue_id=post_in.issue_id, content=post_in.content, state=new_state)


@app.delete("/api/v1.0/posts/{id}", status_code=204)
def delete_post(id: int, request: Request):
    session = request.app.state.cassandra_session
    row = session.execute("SELECT issue_id FROM tbl_post WHERE id=%s ALLOW FILTERING", (id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Post not found")
    session.execute("DELETE FROM tbl_post WHERE issue_id=%s AND id=%s", (row.issue_id, id))
    return Response(status_code=204)