# publisher/app/controllers/comment_controller.py
import httpx
from fastapi import APIRouter, HTTPException
from fastapi.responses import JSONResponse

router = APIRouter(prefix="/api/v1.0/comments")
DISCUSSION_URL = "http://localhost:24130/api/v1.0/comments"

@router.post("", status_code=201)
def create(request_body: dict):
    r = httpx.post(DISCUSSION_URL, json=request_body)
    return JSONResponse(status_code=r.status_code, content=r.json())

@router.get("")
def get_all():
    r = httpx.get(DISCUSSION_URL)
    return JSONResponse(status_code=r.status_code, content=r.json())

@router.get("/{comment_id}")
def get_one(comment_id: int):
    r = httpx.get(f"{DISCUSSION_URL}/{comment_id}")
    return JSONResponse(status_code=r.status_code, content=r.json())

@router.put("/{comment_id}")
def update(comment_id: int, request_body: dict):
    r = httpx.put(f"{DISCUSSION_URL}/{comment_id}", json=request_body)
    return JSONResponse(status_code=r.status_code, content=r.json())

@router.delete("/{comment_id}", status_code=204)
def delete(comment_id: int):
    r = httpx.delete(f"{DISCUSSION_URL}/{comment_id}")
    if r.status_code == 404:
        raise HTTPException(404, detail=r.json())