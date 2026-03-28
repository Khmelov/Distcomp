from fastapi import FastAPI, APIRouter, status, HTTPException
import random
from dto import CommentRequestTo, CommentResponseTo
from database import get_cassandra_session

app = FastAPI(title="Discussion Microservice")
router = APIRouter()
session = get_cassandra_session()


@router.post("/comments", response_model=CommentResponseTo,
             status_code=status.HTTP_201_CREATED)
def create_comment(dto: CommentRequestTo):
    comment_id = random.randint(1, 2000000000)
    query = (
        "INSERT INTO tbl_comment (id, topic_id, content) VALUES (%s, %s, %s)"
    )
    session.execute(query, (comment_id, dto.topicId, dto.content))
    return {"id": comment_id, "content": dto.content, "topicId": dto.topicId}


@router.get("/comments", response_model=list[CommentResponseTo])
def get_comments():
    rows = session.execute("SELECT id, topic_id, content FROM tbl_comment")

    result = []
    for row in rows:
        result.append({
            "id": row["id"],
            "topicId": row["topic_id"],
            "content": row["content"]
        })
    return result


@router.get("/comments/{id}", response_model=CommentResponseTo)
def get_comment(id: int):
    row = session.execute(
        "SELECT id, topic_id, content FROM tbl_comment WHERE id = %s",
        (id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Comment not found")

    return {
        "id": row["id"],
        "topicId": row["topic_id"],
        "content": row["content"]
    }


@router.put("/comments/{id}", response_model=CommentResponseTo)
def update_comment(id: int, dto: CommentRequestTo):
    existing = session.execute(
        "SELECT id FROM tbl_comment WHERE id = %s", (id,)).one()
    if not existing:
        raise HTTPException(status_code=404, detail="Comment not found")

    query = "UPDATE tbl_comment SET content = %s, topic_id = %s WHERE id = %s"
    session.execute(query, (dto.content, dto.topicId, id))
    return {"id": id, "content": dto.content, "topicId": dto.topicId}


@router.delete("/comments/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_comment(id: int):
    existing = session.execute(
        "SELECT id FROM tbl_comment WHERE id = %s", (id,)).one()
    if not existing:
        raise HTTPException(status_code=404, detail="Comment not found")
    session.execute("DELETE FROM tbl_comment WHERE id = %s", (id,))


app.include_router(router, prefix="/api/v1.0")
