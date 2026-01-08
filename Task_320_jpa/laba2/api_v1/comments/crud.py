from sqlalchemy import Result, select
from .schemas import Comment as CommentIN, CommentID
from sqlalchemy.ext.asyncio import AsyncSession
from models import Comment



async def create_comment(
        comment_info: CommentIN,
        session: AsyncSession
):
    comment = Comment(**comment_info.model_dump())
    session.add(comment)
    await session.commit()
    return comment



async def get_comment(
        session: AsyncSession,      
        comment_id: int
):
    stat = select(Comment).where(Comment.id == comment_id)
    result: Result = await session.execute(stat)
    # writers: Sequence = result.scalars().all()
    comment: Comment | None = result.scalar_one_or_none()
    return comment


# async def get_comment_by_title(
#         session: AsyncSession,      
#         comment_title: str
# ):
#     stat = select(comment).where(comment.title == comment_title)
#     result: Result = await session.execute(stat)
#     # writers: Sequence = result.scalars().all()
#     comment: comment | None = result.scalar_one_or_none()

#     return comment


async def delete_comment(
        comment_id: int,
        session: AsyncSession
):
    comment = await get_comment(comment_id=comment_id, session=session)
    if not comment:
        return False
    await session.delete(comment)
    await session.commit()
    return True


async def put_comment(
        comment_info: CommentID,
        session: AsyncSession
):
    comment_id = comment_info.id
    comment_update = CommentIN(**comment_info.model_dump())
    comment = await get_comment(comment_id=comment_id, session=session)
    if not comment:
        return False
    
    for name, value in comment_update.model_dump().items():
        setattr(comment, name, value)
    await session.commit()
    return comment