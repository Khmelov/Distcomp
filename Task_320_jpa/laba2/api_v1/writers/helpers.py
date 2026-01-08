from sqlalchemy.ext.asyncio import AsyncSession
import api_v1.writers.crud as crud
from fastapi import HTTPException, status


async def check_login(login: str, session: AsyncSession):
    """
    Login must be unique
    """
    login_exists = await crud.get_writer_by_login(writer_login=login, session=session)
    if login_exists:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN, detail="Login already exists"
        )
