import time
import random
from fastapi import APIRouter
from fastapi.responses import JSONResponse
from app.schemas.comment import CommentCreate, CommentUpdate
from app import kafka_client

router = APIRouter(prefix="/comments", tags=["comments"])


@router.post("", status_code=201)
def create_comment(data: CommentCreate):
    comment_id = int(time.time() * 1000) + random.randint(0, 999)
    payload = {
        "method": "POST",
        "id": comment_id,
        "issueId": data.issue_id,
        "content": data.content,
        "state": "PENDING",
    }
    # Fire-and-forget: сразу возвращаем ответ, discussion сохранит асинхронно
    kafka_client.send_to_intopic(payload, issue_id=data.issue_id)
    return JSONResponse(status_code=201, content={
        "id": comment_id,
        "issueId": data.issue_id,
        "content": data.content,
        "state": "PENDING",
    })


@router.get("")
def get_comments():
    payload = {"method": "GET_ALL"}
    result = kafka_client.send_and_wait(payload, issue_id=0)
    if result is None:
        return JSONResponse(status_code=504, content={"errorMessage": "timeout", "errorCode": 50401})
    if "error" in result:
        return JSONResponse(status_code=404, content=result["error"])
    return JSONResponse(content=result.get("data", []))


@router.get("/{comment_id}")
def get_comment(comment_id: int):
    payload = {"method": "GET", "id": comment_id}
    result = kafka_client.send_and_wait(payload, issue_id=0)
    if result is None:
        return JSONResponse(status_code=504, content={"errorMessage": "timeout", "errorCode": 50401})
    if "error" in result:
        return JSONResponse(status_code=404, content=result["error"])
    return JSONResponse(content=result.get("data"))


@router.put("/{comment_id}")
def update_comment(comment_id: int, data: CommentUpdate):
    payload = {"method": "PUT", "id": comment_id, "issueId": data.issue_id, "content": data.content}
    result = kafka_client.send_and_wait(payload, issue_id=data.issue_id)
    if result is None:
        return JSONResponse(status_code=504, content={"errorMessage": "timeout", "errorCode": 50401})
    if "error" in result:
        return JSONResponse(status_code=404, content=result["error"])
    return JSONResponse(content=result.get("data"))


@router.delete("/{comment_id}", status_code=204)
def delete_comment(comment_id: int):
    payload = {"method": "DELETE", "id": comment_id}
    result = kafka_client.send_and_wait(payload, issue_id=0)
    if result is None:
        return JSONResponse(status_code=504, content={"errorMessage": "timeout", "errorCode": 50401})
    if "error" in result:
        return JSONResponse(status_code=404, content=result["error"])
    return JSONResponse(status_code=204, content=None)