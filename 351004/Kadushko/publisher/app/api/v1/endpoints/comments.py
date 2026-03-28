import httpx
from fastapi import APIRouter
from fastapi.responses import JSONResponse
from app.schemas.comment import CommentCreate, CommentUpdate

router = APIRouter(prefix="/comments", tags=["comments"])

DISCUSSION_URL = "http://localhost:24130/api/v1.0/comments"


def _proxy(method: str, url: str, **kwargs):
    with httpx.Client(timeout=30.0) as client:
        resp = getattr(client, method)(url, **kwargs)
    return JSONResponse(status_code=resp.status_code, content=resp.json())


@router.get("")
def get_comments():
    return _proxy("get", DISCUSSION_URL)


@router.get("/{comment_id}")
def get_comment(comment_id: int):
    return _proxy("get", f"{DISCUSSION_URL}/{comment_id}")


@router.post("", status_code=201)
def create_comment(data: CommentCreate):
    return _proxy("post", DISCUSSION_URL, json=data.model_dump(by_alias=True))


@router.put("/{comment_id}")
def update_comment(comment_id: int, data: CommentUpdate):
    body = data.model_dump(by_alias=True)
    body["id"] = comment_id
    return _proxy("put", f"{DISCUSSION_URL}/{comment_id}", json=body)


@router.delete("/{comment_id}", status_code=204)
def delete_comment(comment_id: int):
    with httpx.Client(timeout=30.0) as client:
        resp = client.delete(f"{DISCUSSION_URL}/{comment_id}")
    return JSONResponse(status_code=resp.status_code, content=None if resp.status_code == 204 else resp.json())