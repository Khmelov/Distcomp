from .schemas import Issue as IssueIN, IssueBD, IssueID
from models import Issue
from sqlalchemy.ext.asyncio import AsyncSession
from fastapi import HTTPException, status
import api_v1.issues.crud as crud
from api_v1.writers.crud import get_writer


async def check_title(
    title: str,
    session: AsyncSession
):
    """
    Title must be unique 
    """
    title_exists = await crud.get_issue_by_title(
        issue_title=title,
        session=session
    )
    if title_exists:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Title already exists")
    

async def check_writer(
        writer_id: int,
        session: AsyncSession
):
    """
    We can't connect Issue with defunct Writer
    """
    writer_exists = await get_writer(writer_id=writer_id, session=session)
    if not writer_exists:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Writer doesn't exist")

def issue_to_bd(
        issue: IssueIN
) -> IssueBD:
    return IssueBD(
        content= issue.content,
        writer_id= issue.writerId,
        title= issue.title,
    )


def bd_to_id(
        issue: Issue
) -> IssueID:
    return IssueID(
        content=issue.content,
        writerId=issue.writer_id,
        id=issue.id,
        title=issue.title,
    )