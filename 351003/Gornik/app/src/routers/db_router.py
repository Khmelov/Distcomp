from typing import Annotated

from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from database import AsyncSessionLocal


async def get_db() -> AsyncSession:
    async with AsyncSessionLocal() as session:
        yield session


db_dependency = Annotated[AsyncSession, Depends(get_db)]