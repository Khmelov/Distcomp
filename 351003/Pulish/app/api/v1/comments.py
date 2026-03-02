from fastapi import APIRouter, status
from app.dto.comment import CommentRequestTo, CommentResponseTo
from app.services.comment_service import CommentService

router = APIRouter()
service = CommentService()


@router.post("/comments",
             response_model=CommentResponseTo,
             status_code=status.HTTP_201_CREATED)
def create_comment(dto: CommentRequestTo):
    return service.create(dto)


@router.get("/comments", response_model=list[CommentResponseTo])
def get_comments():
    return service.find_all()


@router.get("/comments/{id}", response_model=CommentResponseTo)
def get_comment(id: int):
    return service.find_by_id(id)


@router.put("/comments/{id}", response_model=CommentResponseTo)
def update_comment(id: int, dto: CommentRequestTo):
    return service.update(id, dto)


@router.delete("/comments/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_comment(id: int):
    service.delete(id)
