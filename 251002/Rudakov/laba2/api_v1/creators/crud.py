from sqlalchemy import Result, select
from .schemas import Creator as CreatorIN, CreatorID
from sqlalchemy.ext.asyncio import AsyncSession
from models import Creator


async def create_creator(creator_info: CreatorIN, session: AsyncSession):
    creator = Creator(**creator_info.model_dump())
    session.add(creator)
    await session.commit()
    return creator


async def get_creator(session: AsyncSession, creator_id: int):
    stat = select(Creator).where(Creator.id == creator_id)
    result: Result = await session.execute(stat)
    # creators: Sequence = result.scalars().all()
    creator: Creator | None = result.scalar_one_or_none()

    return creator


async def get_creator_by_login(session: AsyncSession, creator_login: str):
    stat = select(Creator).where(Creator.login == creator_login)
    result: Result = await session.execute(stat)
    # creators: Sequence = result.scalars().all()
    creator: Creator | None = result.scalar_one_or_none()
    return creator


async def delete_creator(creator_id: int, session: AsyncSession):
    creator = await get_creator(creator_id=creator_id, session=session)
    if not creator:
        return False
    await session.delete(creator)
    await session.commit()
    return True


async def put_creator(creator_info: CreatorID, session: AsyncSession):
    creator_id = creator_info.id
    creator_update = CreatorIN(**creator_info.model_dump())
    creator = await get_creator(creator_id=creator_id, session=session)
    if not creator:
        return False

    for name, value in creator_update.model_dump().items():
        setattr(creator, name, value)
    await session.commit()
    return creator
