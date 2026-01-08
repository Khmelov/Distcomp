from sqlalchemy import Result, select
from .schemas import Issue as IssueIN, IssueID, IssueBD 
from sqlalchemy.ext.asyncio import AsyncSession
from models import Issue
# from loguru import logger

# logger.add(
#         sink = "RV2Lab.log",
#         mode="w",
#         encoding="utf-8",
#         format="{time} {level} {comment}",)


async def create_issue(
        issue_info: IssueBD,
        session: AsyncSession
):
    issue = Issue(**issue_info.model_dump())
    session.add(issue)
    await session.commit()
    return issue



async def get_issue(
        session: AsyncSession,      
        issue_id: int
):
    stat = select(Issue).where(Issue.id == issue_id)
    result: Result = await session.execute(stat)
    # writers: Sequence = result.scalars().all()
    issue: Issue | None = result.scalar_one_or_none()

    return issue


async def get_issue_by_title(
        session: AsyncSession,      
        issue_title: str
):
    stat = select(Issue).where(Issue.title == issue_title)
    result: Result = await session.execute(stat)
    # writers: Sequence = result.scalars().all()
    issue: Issue | None = result.scalar_one_or_none()

    return issue


async def delete_issue(
        issue_id: int,
        session: AsyncSession
):
    issue = await get_issue(issue_id=issue_id, session=session)
    if not issue:
        return False
    await session.delete(issue)
    await session.commit()
    return True


async def put_issue(
        issue_info: IssueID,
        session: AsyncSession
):
    issue_id =issue_info.id
    issue_update = IssueIN(**issue_info.model_dump())
    issue = await get_issue(issue_id=issue_id, session=session)
    if not issue:
        return False
    
    for name, value in issue_update.model_dump().items():
        setattr(issue, name, value)
    await session.commit()
    return issue