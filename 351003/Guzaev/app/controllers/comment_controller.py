from fastapi import APIRouter, status, Response
from typing import List
from app.dtos.comment_dto import CommentRequestTo, CommentResponseTo
from app.services.comment_service import CommentService

router = APIRouter(prefix="/api/v1.0/comments", tags=["comments"])
service = CommentService()

@router.post("", status_code=status.HTTP_201_CREATED, response_model=CommentResponseTo)
def create_comment(dto: CommentRequestTo):
    return service.create(dto)

@router.get("", response_model=List[CommentResponseTo])
def get_comments():
    return service.get_all()

@router.get("/{id}", response_model=CommentResponseTo)
def get_comment(id: int):
    return service.get_by_id(id)

@router.put("/{id}", response_model=CommentResponseTo)
def update_comment(id: int, dto: CommentRequestTo):
    return service.update(id, dto)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_comment(id: int):
    service.delete(id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
