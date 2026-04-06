from fastapi import APIRouter, status
from fastapi.responses import JSONResponse
from typing import List
from app.schemas.comment import CommentCreate, CommentUpdate, CommentResponse
import app.services.comment_service as svc

router = APIRouter(prefix="/comments", tags=["comments"])


@router.get("", status_code=200)
def get_comments():
    results = svc.get_all()
    return JSONResponse(content=[r.model_dump(by_alias=True) for r in results])


@router.get("/{comment_id}", status_code=200)
def get_comment(comment_id: int):
    result = svc.get_by_id(comment_id)
    if not result:
        return JSONResponse(status_code=404, content={"errorMessage": f"Comment {comment_id} not found", "errorCode": 40401})
    return JSONResponse(content=result.model_dump(by_alias=True))


@router.post("", status_code=201)
def create_comment(data: CommentCreate):
    result = svc.create(data)
    return JSONResponse(status_code=201, content=result.model_dump(by_alias=True))


@router.put("/{comment_id}", status_code=200)
def update_comment(comment_id: int, data: CommentUpdate):
    result = svc.update(comment_id, data)
    if not result:
        return JSONResponse(status_code=404, content={"errorMessage": f"Comment {comment_id} not found", "errorCode": 40401})
    return JSONResponse(content=result.model_dump(by_alias=True))


@router.delete("/{comment_id}", status_code=204)
def delete_comment(comment_id: int):
    found = svc.delete(comment_id)
    if not found:
        return JSONResponse(status_code=404, content={"errorMessage": f"Comment {comment_id} not found", "errorCode": 40401})
