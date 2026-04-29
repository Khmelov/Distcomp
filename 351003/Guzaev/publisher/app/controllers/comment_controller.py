from fastapi import APIRouter, HTTPException
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from typing import Optional
from services.kafka_comment_service import send_and_wait

router = APIRouter(prefix="/api/v1.0/comments")


class CommentRequest(BaseModel):
    id: Optional[int] = None
    tweetId: int
    content: str
    country: Optional[str] = "Belarus"


class CommentUpdateRequest(BaseModel):
    id: int
    tweetId: int
    content: str
    country: Optional[str] = "Belarus"


@router.post("/", status_code=201)
def create(dto: CommentRequest):
    import uuid
    generated_id = int(uuid.uuid4().int % 1_000_000_000)
    result = send_and_wait("CREATE", {
        "id": generated_id,
        "tweetId": dto.tweetId,
        "content": dto.content,
        "country": dto.country
    }, tweet_id=dto.tweetId)
    if result is None or "errorCode" in result:
        return JSONResponse(status_code=400, content=result or {"errorMessage": "Timeout", "errorCode": 40001})
    return JSONResponse(status_code=201, content=result)


@router.get("/")
def get_all():
    result = send_and_wait("GET_ALL", {})
    if result is None:
        return []
    return result


@router.get("/{comment_id}", status_code=200)
def get_one(comment_id: str):
    try:
        cid = int(comment_id)
    except (ValueError, TypeError):
        raise HTTPException(status_code=404, detail={"errorMessage": "Comment not found", "errorCode": 40404})
    result = send_and_wait("GET", {"id": cid})
    if result is None or "errorCode" in result:
        raise HTTPException(status_code=404, detail={"errorMessage": "Comment not found", "errorCode": 40404})
    return result


@router.put("/", status_code=200)
def update_comment(dto: CommentUpdateRequest):
    result = send_and_wait("UPDATE", {
        "id": dto.id,
        "tweetId": dto.tweetId,
        "content": dto.content,
        "country": dto.country
    }, tweet_id=dto.tweetId)
    if result is None or "errorCode" in result:
        return JSONResponse(status_code=404, content=result or {"errorMessage": "Not found", "errorCode": 40404})
    return JSONResponse(status_code=200, content=result)


@router.put("/{comment_id}", status_code=200)
def update(comment_id: str, dto: CommentUpdateRequest):
    result = send_and_wait("UPDATE", {
        "id": comment_id,
        "tweetId": dto.tweetId,
        "content": dto.content,
        "country": dto.country
    }, tweet_id=dto.tweetId)
    if result is None or "errorCode" in result:
        return JSONResponse(status_code=404, content=result or {"errorMessage": "Not found", "errorCode": 40404})
    return JSONResponse(status_code=200, content=result)


@router.delete("/{comment_id}", status_code=204)
def delete(comment_id: str):
    try:
        cid = int(comment_id)
    except (ValueError, TypeError):
        raise HTTPException(status_code=404, detail={"errorMessage": "Comment not found", "errorCode": 40404})
    result = send_and_wait("DELETE", {"id": cid})
    if result is None or (isinstance(result, dict) and "errorCode" in result):
        raise HTTPException(status_code=404, detail={"errorMessage": "Comment not found", "errorCode": 40404})
    return JSONResponse(status_code=204, content=None)