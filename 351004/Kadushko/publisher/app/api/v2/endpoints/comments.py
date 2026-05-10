import time, random
from fastapi import APIRouter, Depends
from fastapi.responses import JSONResponse
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from sqlalchemy.orm import Session
from app.database import get_db
from app.schemas.comment import CommentCreate, CommentUpdate
from app import kafka_client, cache
from app.auth import get_current_user, security

router = APIRouter(prefix="/comments", tags=["comments-v2"])


@router.get("")
def get_comments(credentials: HTTPAuthorizationCredentials = Depends(security)):
    get_current_user(credentials)
    import httpx
    cache_key = "comments:all:0:10000:id:asc"
    cached = cache.cache_get(cache_key)
    if cached is not None:
        return JSONResponse(content=cached)
    resp = httpx.get("http://localhost:24130/api/v1.0/comments", timeout=1.5)
    data = resp.json()
    cache.cache_set(cache_key, data)
    return JSONResponse(content=data)


@router.get("/{comment_id}")
def get_comment(comment_id: int, credentials: HTTPAuthorizationCredentials = Depends(security)):
    get_current_user(credentials)
    cache_key = f"comments:{comment_id}"
    cached = cache.cache_get(cache_key)
    if cached is not None:
        return JSONResponse(content=cached)
    result = kafka_client.send_and_wait({"method": "GET", "id": comment_id}, issue_id=0)
    if result is None:
        return JSONResponse(status_code=504, content={"errorMessage": "timeout", "errorCode": 50401})
    if "error" in result:
        return JSONResponse(status_code=404, content=result["error"])
    cache.cache_set(cache_key, result.get("data"))
    return JSONResponse(content=result.get("data"))


@router.post("", status_code=201)
def create_comment(data: CommentCreate, credentials: HTTPAuthorizationCredentials = Depends(security)):
    get_current_user(credentials)
    comment_id = int(time.time() * 1000) + random.randint(0, 999)
    payload = {"method": "POST", "id": comment_id, "issueId": data.issue_id, "content": data.content, "state": "PENDING"}
    kafka_client.send_to_intopic(payload, issue_id=data.issue_id)
    data_out = {"id": comment_id, "issueId": data.issue_id, "content": data.content, "state": "PENDING"}
    time.sleep(1.0)
    cache.cache_set(f"comments:{comment_id}", data_out)
    cache.cache_delete_pattern("comments:all:*")
    return JSONResponse(status_code=201, content=data_out)


@router.put("/{comment_id}")
def update_comment(comment_id: int, data: CommentUpdate, credentials: HTTPAuthorizationCredentials = Depends(security)):
    get_current_user(credentials)
    cache.cache_delete(f"comments:{comment_id}")
    cache.cache_delete_pattern("comments:all:*")
    result = kafka_client.send_and_wait({"method": "PUT", "id": comment_id, "issueId": data.issue_id, "content": data.content}, issue_id=data.issue_id)
    if result is None:
        return JSONResponse(status_code=504, content={"errorMessage": "timeout", "errorCode": 50401})
    if "error" in result:
        return JSONResponse(status_code=404, content=result["error"])
    cache.cache_set(f"comments:{comment_id}", result.get("data"))
    return JSONResponse(content=result.get("data"))


@router.delete("/{comment_id}", status_code=204)
def delete_comment(comment_id: int, credentials: HTTPAuthorizationCredentials = Depends(security)):
    get_current_user(credentials)
    cache.cache_delete(f"comments:{comment_id}")
    cache.cache_delete_pattern("comments:all:*")
    result = kafka_client.send_and_wait({"method": "DELETE", "id": comment_id}, issue_id=0)
    if result is None:
        return JSONResponse(status_code=504, content={"errorMessage": "timeout", "errorCode": 50401})
    if "error" in result:
        return JSONResponse(status_code=404, content=result["error"])
    return JSONResponse(status_code=204, content=None)
