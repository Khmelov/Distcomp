from fastapi import APIRouter
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from typing import Optional
from services.kafka_comment_service import send_and_wait

router = APIRouter(prefix="/api/v1.0/comments")

class CommentRequest(BaseModel):
    tweetId: int
    content: str
    country: Optional[str] = "Belarus"

class CommentUpdateRequest(BaseModel):
    id: int
    tweetId: int
    content: str
    country: Optional[str] = "Belarus"

@router.post("", status_code=201)
def create(dto: CommentRequest):
    import uuid
    generated_id = int(uuid.uuid4().int % 1_000_000_000)
    result = send_and_wait("CREATE", {
        "id": generated_id, "tweetId": dto.tweetId,
        "content": dto.content, "country": dto.country
    }, tweet_id=dto.tweetId)
    if "errorCode" in result:
        return JSONResponse(status_code=400, content=result)
    return JSONResponse(status_code=201, content=result)

@router.get("")
def get_all():
    result = send_and_wait("GET_ALL", {})
    if isinstance(result, dict) and "errorCode" in result:
        return JSONResponse(status_code=500, content=result)
    return JSONResponse(status_code=200, content=result)

@router.get("/{comment_id}")
def get_one(comment_id: str):
    result = send_and_wait("GET", {"id": comment_id})
    if "errorCode" in result:
        return JSONResponse(status_code=404, content=result)
    return JSONResponse(status_code=200, content=result)

@router.put("/{comment_id}")
def update(comment_id: str, dto: CommentUpdateRequest):
    result = send_and_wait("UPDATE", {
        "id": comment_id, "tweetId": dto.tweetId,
        "content": dto.content, "country": dto.country
    }, tweet_id=dto.tweetId)
    if "errorCode" in result:
        return JSONResponse(status_code=404, content=result)
    return JSONResponse(status_code=200, content=result)

@router.delete("/{comment_id}", status_code=204)
def delete(comment_id: str):
    result = send_and_wait("DELETE", {"id": comment_id})
    if isinstance(result, dict) and "errorCode" in result:
        return JSONResponse(status_code=404, content=result)
    return JSONResponse(status_code=204, content=None)