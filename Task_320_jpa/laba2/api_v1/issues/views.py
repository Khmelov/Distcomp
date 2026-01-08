from fastapi import APIRouter, status, HTTPException, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from loguru import logger

from db_helper import db_helper

from .schemas import Issue, IssueID
import api_v1.issues.crud as crud

from .helpers import issue_to_bd, bd_to_id, check_title, check_writer


logger.add(
    sink="app.log",
    mode="w",
    encoding="utf-8",
    format="{time} {level} {issues}",
)

router = APIRouter(prefix="/issues")
#не точно issue?

costyl_id = 0


@router.get("/{get_id}", status_code=status.HTTP_200_OK, response_model=IssueID)
async def issue_by_id(
    get_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"GET definite Issue with id: {get_id}")
    issue = await crud.get_issue(session=session, issue_id=get_id)
    if not issue:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such Issue"
        )
    return bd_to_id(issue)


@router.get("", status_code=status.HTTP_200_OK, response_model=IssueID)
async def issue(session: AsyncSession = Depends(db_helper.session_dependency)):
    logger.info("GET Marker")
    global costyl_id

    issue = await crud.get_issue(session=session, issue_id=costyl_id)
    if not issue:
        return {
            "id": 0,
            "writerId": 0,
            "title": "sdsds",
            "content": "dsdsds",
        }
    return bd_to_id(issue)


@router.post("", status_code=status.HTTP_201_CREATED, response_model=IssueID)
async def create_issue(
    issue_info: Issue, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"POST Issue with body: {issue_info.model_dump()}")

    await check_writer(
        writer_id=issue_info.writerId,
        session=session,
    )

    await check_title(session=session, title=issue_info.title)

    issue_info = issue_to_bd(issue_info)
    issue = await crud.create_issue(session=session, issue_info=issue_info)

    if not issue:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Incorrect data"
        )

    global costyl_id
    costyl_id = issue.id

    return bd_to_id(issue=issue)


@router.delete("/{delete_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_issue(
    delete_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):

    logger.info(f"DELETE Issue with ID: {delete_id}")
    delete_state = await crud.delete_issue(issue_id=delete_id, session=session)
    if not delete_state:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such Issue"
        )
    return


@router.put("", status_code=status.HTTP_200_OK, response_model=IssueID)
async def put_issue(
    issue_info: IssueID, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"PUT Issue with body: {issue_info.model_dump()}")
    issue = await crud.put_issue(issue_info=issue_info, session=session)
    if not issue:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Invlaid PUT data"
        )
    return issue
