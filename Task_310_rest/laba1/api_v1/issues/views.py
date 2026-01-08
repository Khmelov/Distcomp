from fastapi import APIRouter, HTTPException, status
from loguru import logger
from .schemas import Issue, IssueID
from api_v1.util import clear_storage


logger.add(
        sink = "app.log",
        mode="w",
        encoding="utf-8",
        format="{time} {level} {issues}",)

router = APIRouter()
prefix = "/issues" 

current_issue = {
    "id": 0,
    "writerId": 0,
    "title": "",
    "content": "",
}



@router.get(prefix + "/{get_id}",
            status_code=status.HTTP_200_OK,
            response_model=IssueID)
async def issue_by_id(
    get_id: int
):
    global current_issue
    logger.info(f"GET issue by id {get_id}")
    if get_id != current_issue["id"]:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such issue"
        )
    return IssueID.model_validate(current_issue)


@router.get(prefix,
            status_code=status.HTTP_200_OK,
            response_model=IssueID)
async def issue():
    global current_issue
    logger.info("GET issue")
    return IssueID.model_validate(current_issue)


@router.post(prefix,
            status_code= status.HTTP_201_CREATED,
            response_model=IssueID)
async def create_issue(
    issue: Issue
):
    global current_issue
    logger.info(f"POST writer with body: {issue.model_dump()}")
    current_issue = {"id":0, **issue.model_dump() }
    return IssueID.model_validate(current_issue)


@router.delete(prefix + "/{delete_id}",
               status_code=status.HTTP_204_NO_CONTENT)
async def delete_issue(
    delete_id: int
):
    global current_issue
    logger.info(f"DELETE writer with ID: {delete_id}")
    if delete_id != current_issue["id"]:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such issue"
        )
    current_issue = clear_storage(current_issue)
    current_issue["writerId"] = 100000
    return 


@router.put(prefix,
            status_code=status.HTTP_200_OK,
            response_model=IssueID)
async def put_issue(
    issue: IssueID
):
    global current_issue
    logger.info(f"PUT writer with body: {issue.model_dump()}")
    if issue.title == "x":
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invlaid PUT data"
        )
    current_issue = {**issue.model_dump()}
    return issue

