from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import Optional
from repositories.comment_repository import CommentRepository
from models.comment import Comment

router = APIRouter(prefix="/api/v1.0/comments")
repo = CommentRepository()

class CommentRequest(BaseModel):
    tweetId: int
    content: str
    country: Optional[str] = "Belarus"

class CommentResponse(BaseModel):
    id: int
    tweetId: int
    content: str
    country: str

@router.post("", response_model=CommentResponse, status_code=201)
def create(dto: CommentRequest):
    c = repo.create(Comment(id=0, tweet_id=dto.tweetId, country=dto.country, content=dto.content))
    return CommentResponse(id=c.id, tweetId=c.tweet_id, content=c.content, country=c.country)

@router.get("", response_model=list[CommentResponse])
def get_all():
    return [CommentResponse(id=c.id, tweetId=c.tweet_id, content=c.content, country=c.country) for c in repo.get_all()]

@router.get("/{comment_id}", response_model=CommentResponse)
def get_one(comment_id: str):
    c = repo.get_by_id(comment_id)
    if not c:
        raise HTTPException(404, detail={"errorMessage": "Comment not found", "errorCode": 40404})
    return CommentResponse(id=c.id, tweetId=c.tweet_id, content=c.content, country=c.country)

@router.put("/{comment_id}", response_model=CommentResponse)
def update(comment_id: str, dto: CommentRequest):
    c = repo.get_by_id(comment_id)
    if not c:
        raise HTTPException(404, detail={"errorMessage": "Comment not found", "errorCode": 40404})
    c.content = dto.content
    c.tweet_id = dto.tweetId
    repo.update(c)
    return CommentResponse(id=c.id, tweetId=c.tweet_id, content=c.content, country=c.country)

@router.delete("/{comment_id}", status_code=204)
def delete(comment_id: str):
    if not repo.delete(comment_id):
        raise HTTPException(404, detail={"errorMessage": "Comment not found", "errorCode": 40404})