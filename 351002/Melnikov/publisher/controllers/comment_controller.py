from fastapi import APIRouter, Response, status

from ..dto.comment_dto import CommentRequestTo, CommentResponseTo
from ..services.comment_service import CommentService

router = APIRouter(prefix="/api/v1.0/comments", tags=["comment"])
service = CommentService()


@router.post("", response_model=CommentResponseTo, status_code=status.HTTP_201_CREATED)
def create_comment(dto: CommentRequestTo):
    return service.create(dto)


@router.get("", response_model=list[CommentResponseTo], status_code=status.HTTP_200_OK)
def get_comments():
    return service.get_all()


@router.get("/{comment_id}", response_model=CommentResponseTo, status_code=status.HTTP_200_OK)
def get_comment(comment_id: int):
    return service.get_by_id(comment_id)


@router.put("/{comment_id}", response_model=CommentResponseTo, status_code=status.HTTP_200_OK)
def update_comment(comment_id: int, dto: CommentRequestTo):
    return service.update(comment_id, dto)


@router.delete("/{comment_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_comment(comment_id: int):
    service.delete(comment_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)