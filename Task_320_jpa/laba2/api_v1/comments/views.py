from fastapi import APIRouter, status, HTTPException, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from loguru import logger

from db_helper import db_helper

from .schemas import Comment, CommentID
import api_v1.comments.crud as crud
from .helper import check_issue


logger.add(
    sink="app.log",
    mode="w",
    encoding="utf-8",
    format="{time} {level} {comments}",
)

router = APIRouter(
    prefix="/comments",
)

global costyl_id
costyl_id = 0


@router.get("/{get_id}", status_code=status.HTTP_200_OK, response_model=CommentID)
async def comment_by_id(
    get_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"GET definite Comment with id: {get_id}")
    comment = await crud.get_comment(session=session, comment_id=get_id)
    if not comment:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such comment"
        )
    return comment


@router.get("", status_code=status.HTTP_200_OK, response_model=CommentID)
async def comment(session: AsyncSession = Depends(db_helper.session_dependency)):
    logger.info("GET Comment")
    global costyl_id

    comment = await crud.get_comment(session=session, comment_id=costyl_id)
    if not comment:
        return {
            "id": 0,
            "issueId": 0,
            "content": " " * 2,
        }
    return comment


@router.post("", status_code=status.HTTP_201_CREATED, response_model=CommentID)
async def create_comment(
    comment_info: Comment, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"POST Comment with body: {comment_info.model_dump()}")

    await check_issue(session=session, issue_id=comment_info.issueId)

    comment = await crud.create_comment(session=session, comment_info=comment_info)

    if not comment:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Incorrect data"
        )

    global costyl_id
    costyl_id = comment.id
    return comment


@router.delete("/{delete_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_comment(
    delete_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):

    logger.info(f"DELETE comment with ID: {delete_id}")
    delete_state = await crud.delete_comment(comment_id=delete_id, session=session)
    if not delete_state:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such Comment"
        )
    return


@router.put("", status_code=status.HTTP_200_OK, response_model=CommentID)
async def put_writer(
    comment: CommentID, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"PUT Comment with body: {comment.model_dump()}")
    await check_issue(session=session, issue_id=comment.issueId)
    comment = await crud.put_comment(comment_info=comment, session=session)
    if not comment:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Invlaid PUT data"
        )
    return comment
