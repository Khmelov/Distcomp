import time
import random
from fastapi import APIRouter
from fastapi.responses import JSONResponse
from app.schemas.comment import CommentCreate, CommentUpdate
from app import kafka_client, cache

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
    kafka_client.send_to_intopic(payload, issue_id=data.issue_id)
    data_out = {
        "id": comment_id,
        "issueId": data.issue_id,
        "content": data.content,
        "state": "PENDING",
    }
    # Ждём чтобы discussion успел сохранить в Cassandra/MongoDB
    time.sleep(1.0)
    cache.cache_set(f"comments:{comment_id}", data_out)
    cache.cache_delete_pattern("comments:all:*")
    return JSONResponse(status_code=201, content=data_out)


@router.get("")
def get_comments():
    cache_key = "comments:all:0:10000:id:asc"
    cached = cache.cache_get(cache_key)
    if cached is not None:
        return JSONResponse(content=cached)
    import httpx
    resp = httpx.get("http://localhost:24130/api/v1.0/comments", timeout=1.5)
    data = resp.json()
    cache.cache_set(cache_key, data)
    return JSONResponse(content=data)


@router.get("/{comment_id}")
def get_comment(comment_id: int):
    cache_key = f"comments:{comment_id}"
    cached = cache.cache_get(cache_key)
    if cached is not None:
        return JSONResponse(content=cached)
    payload = {"method": "GET", "id": comment_id}
    result = kafka_client.send_and_wait(payload, issue_id=0, timeout=1.5)
    if result is None:
        return JSONResponse(status_code=504, content={"errorMessage": "timeout", "errorCode": 50401})
    if "error" in result:
        return JSONResponse(status_code=404, content=result["error"])
    data = result.get("data")
    cache.cache_set(cache_key, data)
    return JSONResponse(content=data)


@router.put("/{comment_id}")
def update_comment(comment_id: int, data: CommentUpdate):
    # Инвалидируем кеш ДО запроса чтобы GET не вернул старое
    cache.cache_delete(f"comments:{comment_id}")
    cache.cache_delete_pattern("comments:all:*")
    payload = {"method": "PUT", "id": comment_id, "issueId": data.issue_id, "content": data.content}
    result = kafka_client.send_and_wait(payload, issue_id=data.issue_id, timeout=1.5)
    if result is None:
        return JSONResponse(status_code=504, content={"errorMessage": "timeout", "errorCode": 50401})
    if "error" in result:
        return JSONResponse(status_code=404, content=result["error"])
    data_out = result.get("data")
    cache.cache_set(f"comments:{comment_id}", data_out)
    return JSONResponse(content=data_out)


@router.delete("/{comment_id}", status_code=204)
def delete_comment(comment_id: int):
    cache.cache_delete(f"comments:{comment_id}")
    cache.cache_delete_pattern("comments:all:*")
    payload = {"method": "DELETE", "id": comment_id}
    result = kafka_client.send_and_wait(payload, issue_id=0, timeout=1.5)
    if result is None:
        return JSONResponse(status_code=504, content={"errorMessage": "timeout", "errorCode": 50401})
    if "error" in result:
        return JSONResponse(status_code=404, content=result["error"])
    return JSONResponse(status_code=204, content=None)