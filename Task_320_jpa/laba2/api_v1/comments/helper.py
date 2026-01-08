from sqlalchemy.ext.asyncio import AsyncSession
from fastapi import HTTPException, status
from api_v1.issues.crud import get_issue

async def check_issue(
        issue_id: int,
        session: AsyncSession
):
    """
    We can't connect Comment with defunct Issue
    """
    issue_exists = await get_issue(issue_id=issue_id, session=session)
    if not issue_exists:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Issue doesn't exist")