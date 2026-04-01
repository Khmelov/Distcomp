from fastapi import APIRouter, HTTPException, status
from sqlalchemy import select

from dto import CommentResponseTo, CommentRequestTo
from models import Comment
from routers.db_router import db_dependency

router = APIRouter(
    prefix="/api/v1.0/comments",
    tags=["comments"],
)

@router.get("", response_model=list[CommentResponseTo])
async def get_comments(db: db_dependency):
    comments = await db.execute(select(Comment))
    comments = comments.scalars().all()
    return comments


@router.post("", response_model=CommentResponseTo, status_code=201)
async def create_comment(data: CommentRequestTo, db: db_dependency):
    try:
        comment = Comment(**data.dict())
        db.add(comment)
        await db.commit()
        await db.refresh(comment)
        return comment
    except Exception as e:
        await db.rollback()
        # Теперь status распознается корректно, и вернется 4xx (400)
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid association: tweetId does not exist."
        )

@router.put("/{id}")
async def update_comment(id: int, data: CommentRequestTo, db: db_dependency):
    comment = await db.execute(select(Comment).where(Comment.id == id))
    comment = comment.scalars().first()
    for key, value in data.dict().items():
        setattr(comment, key, value)
    db.add(comment)
    await db.commit()
    return comment

@router.get("/{id}", response_model=CommentResponseTo)
async def get_comment(id: int, db: db_dependency):
    comment = await db.execute(select(Comment).where(Comment.id == id))
    comment = comment.scalars().first()
    return comment

@router.delete("/{id}", status_code=204)
async def delete_comment(id: int, db: db_dependency):
    comment = await db.execute(select(Comment).where(Comment.id == id))
    comment = comment.scalars().first()
    if not comment:
        raise HTTPException(status_code=404, detail="Comment not found")
    await db.delete(comment)
    await db.commit()