from sqlalchemy import Result, select
from .schemas import Marker as MarkerIN, MarkerID
from sqlalchemy.ext.asyncio import AsyncSession
from models import Marker



async def create_marker(
        marker_info: MarkerIN,
        session: AsyncSession
):
    marker = Marker(**marker_info.model_dump())
    session.add(marker)
    await session.commit()
    return marker


async def get_marker(
        session: AsyncSession,      
        marker_id: int
):
    stat = select(Marker).where(Marker.id == marker_id)
    result: Result = await session.execute(stat)
    # writers: Sequence = result.scalars().all()
    comment: Marker | None = result.scalar_one_or_none()
    return comment


async def delete_marker(
        marker_id: int,
        session: AsyncSession
):
    marker = await get_marker(marker_id=marker_id, session=session)
    if not marker:
        return False
    await session.delete(marker)
    await session.commit()
    return True



async def put_marker(
        marker_info: MarkerID,
        session: AsyncSession
):
    marker_id = marker_info.id
    marker_update = MarkerIN(**marker_info.model_dump())
    marker = await get_marker(marker_id=marker_id, session=session)
    if not marker:
        return False
    
    for name, value in marker_update.model_dump().items():
        setattr(marker, name, value)
    await session.commit()
    return marker