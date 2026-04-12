from fastapi import FastAPI, APIRouter, status, HTTPException
from contextlib import asynccontextmanager
from dto import CommentRequestTo, CommentResponseTo
from database import get_cassandra_session
from config import settings
from kafka_worker import start_kafka_worker

session = get_cassandra_session()


@asynccontextmanager
async def lifespan(app: FastAPI):
    start_kafka_worker(settings.KAFKA_BOOTSTRAP_SERVERS, session)
    yield


app = FastAPI(title="Discussion Microservice", lifespan=lifespan)
router = APIRouter()


def _row_to_dict(row: dict) -> dict:
    return {
        "id": row["id"],
        "topicId": row["topic_id"],
        "content": row["content"],
        "state": row.get("state") or "APPROVE",
    }


@router.post("/comments", response_model=CommentResponseTo,
             status_code=status.HTTP_201_CREATED)
def create_comment(dto: CommentRequestTo):
    import random
    comment_id = dto.id if dto.id else random.randint(1, 2_000_000_000)
    session.execute(
        "INSERT INTO tbl_comment (id, topic_id, content, state) VALUES (%s, %s, %s, %s)",
        (comment_id, dto.topicId, dto.content, "APPROVE"),
    )
    return {"id": comment_id, "content": dto.content,
            "topicId": dto.topicId, "state": "APPROVE"}


@router.get("/comments", response_model=list[CommentResponseTo])
def get_comments():
    rows = session.execute(
        "SELECT id, topic_id, content, state FROM tbl_comment"
    )
    return [_row_to_dict(r) for r in rows]


@router.get("/comments/{id}", response_model=CommentResponseTo)
def get_comment(id: int):
    try:
        row = session.execute(
            "SELECT id, topic_id, content, state FROM tbl_comment WHERE id = %s",
            (id,)
        ).one()
    except Exception:
        raise HTTPException(status_code=404, detail="Comment not found")
    if not row:
        raise HTTPException(status_code=404, detail="Comment not found")
    return _row_to_dict(row)


@router.put("/comments/{id}", response_model=CommentResponseTo)
def update_comment(id: int, dto: CommentRequestTo):
    try:
        existing = session.execute(
            "SELECT id FROM tbl_comment WHERE id = %s", (id,)
        ).one()
    except Exception:
        raise HTTPException(status_code=404, detail="Comment not found")
    if not existing:
        raise HTTPException(status_code=404, detail="Comment not found")
    session.execute(
        "UPDATE tbl_comment SET content = %s, topic_id = %s WHERE id = %s",
        (dto.content, dto.topicId, id),
    )
    return {"id": id, "content": dto.content,
            "topicId": dto.topicId, "state": "APPROVE"}


@router.delete("/comments/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_comment(id: int):
    try:
        existing = session.execute(
            "SELECT id FROM tbl_comment WHERE id = %s", (id,)
        ).one()
    except Exception:
        raise HTTPException(status_code=404, detail="Comment not found")
    if not existing:
        raise HTTPException(status_code=404, detail="Comment not found")
    session.execute("DELETE FROM tbl_comment WHERE id = %s", (id,))


app.include_router(router, prefix="/api/v1.0")
