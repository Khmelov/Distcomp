from sqlalchemy import Result, select
from .schemas import Sticker as StickerIN, StickerID
from sqlalchemy.ext.asyncio import AsyncSession
from models import Sticker



async def create_sticker(
        sticker_info: StickerIN,
        session: AsyncSession
):
    sticker = Sticker(**sticker_info.model_dump())
    session.add(sticker)
    await session.commit()
    return sticker


async def get_sticker(
        session: AsyncSession,      
        sticker_id: int
):
    stat = select(Sticker).where(Sticker.id == sticker_id)
    result: Result = await session.execute(stat)
    # creators: Sequence = result.scalars().all()
    message: Sticker | None = result.scalar_one_or_none()
    return message


async def delete_sticker(
        sticker_id: int,
        session: AsyncSession
):
    sticker = await get_sticker(sticker_id=sticker_id, session=session)
    if not sticker:
        return False
    await session.delete(sticker)
    await session.commit()
    return True



async def put_sticker(
        sticker_info: StickerID,
        session: AsyncSession
):
    sticker_id = sticker_info.id
    sticker_update = StickerIN(**sticker_info.model_dump())
    sticker = await get_sticker(sticker_id=sticker_id, session=session)
    if not sticker:
        return False
    
    for name, value in sticker_update.model_dump().items():
        setattr(sticker, name, value)
    await session.commit()
    return sticker