import os
import time
from contextlib import asynccontextmanager

from fastapi import FastAPI, Request, HTTPException, Response
from cassandra.cluster import Cluster

from src.schemas import PostRequestTo, PostResponseTo


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
                    CREATE TABLE IF NOT EXISTS tbl_post (
                                                            issue_id bigint,
                                                            id bigint,
                                                            country text,
                                                            content text,
                                                            PRIMARY KEY ((issue_id), id)
                        ) WITH CLUSTERING ORDER BY (id ASC);
                    """)

    app.state.cassandra_session = session
    yield
    cluster.shutdown()


app = FastAPI(lifespan=lifespan, title="Discussion Service")


@app.post("/api/v1.0/posts", response_model=PostResponseTo, status_code=201)
def create_post(post_in: PostRequestTo, request: Request):
    session = request.app.state.cassandra_session
    post_id = int(time.time() * 1000)
    country = "RU"

    query = "INSERT INTO tbl_post (issue_id, id, country, content) VALUES (%s, %s, %s, %s)"
    session.execute(query, (post_in.issue_id, post_id, country, post_in.content))
    return PostResponseTo(id=post_id, content=post_in.content, issue_id=post_in.issue_id)


@app.get("/api/v1.0/posts", response_model=list[PostResponseTo])
def get_posts(request: Request, issueId: int = None):
    session = request.app.state.cassandra_session
    if issueId:
        rows = session.execute("SELECT id, issue_id, content FROM tbl_post WHERE issue_id=%s", (issueId,))
    else:
        rows = session.execute("SELECT id, issue_id, content FROM tbl_post")

    return [PostResponseTo(id=row.id, issue_id=row.issue_id, content=row.content) for row in rows]


@app.get("/api/v1.0/posts/{id}", response_model=PostResponseTo)
def get_post(id: int, request: Request):
    session = request.app.state.cassandra_session

    row = session.execute("SELECT id, issue_id, content FROM tbl_post WHERE id=%s ALLOW FILTERING", (id,)).one()

    if not row:
        raise HTTPException(status_code=404, detail="Post not found")

    return PostResponseTo(id=row.id, issue_id=row.issue_id, content=row.content)


@app.put("/api/v1.0/posts/{id}", response_model=PostResponseTo)
def update_post(id: int, post_in: PostRequestTo, request: Request):
    session = request.app.state.cassandra_session

    row = session.execute("SELECT issue_id FROM tbl_post WHERE id=%s ALLOW FILTERING", (id,)).one()

    if not row:
        raise HTTPException(status_code=404, detail="Post not found")

    session.execute("UPDATE tbl_post SET content=%s WHERE issue_id=%s AND id=%s", (post_in.content, row.issue_id, id))
    return PostResponseTo(id=id, issue_id=post_in.issue_id, content=post_in.content)


@app.delete("/api/v1.0/posts/{id}", status_code=204)
def delete_post(id: int, request: Request):
    session = request.app.state.cassandra_session

    row = session.execute("SELECT issue_id FROM tbl_post WHERE id=%s ALLOW FILTERING", (id,)).one()

    if not row:
        raise HTTPException(status_code=404, detail="Post not found")

    session.execute("DELETE FROM tbl_post WHERE issue_id=%s AND id=%s", (row.issue_id, id))

    return Response(status_code=204)
