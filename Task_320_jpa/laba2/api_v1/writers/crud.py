from sqlalchemy import Result, select
from .schemas import Writer as WriterIN, WriterID
from sqlalchemy.ext.asyncio import AsyncSession
from models import Writer


async def create_writer(writer_info: WriterIN, session: AsyncSession):
    writer = Writer(**writer_info.model_dump())
    session.add(writer)
    await session.commit()
    return writer


async def get_writer(session: AsyncSession, writer_id: int):
    stat = select(Writer).where(Writer.id == writer_id)
    result: Result = await session.execute(stat)
    # writers: Sequence = result.scalars().all()
    writer: Writer | None = result.scalar_one_or_none()

    return writer


async def get_writer_by_login(session: AsyncSession, writer_login: str):
    stat = select(Writer).where(Writer.login == writer_login)
    result: Result = await session.execute(stat)
    # writers: Sequence = result.scalars().all()
    writer: Writer | None = result.scalar_one_or_none()
    return writer


async def delete_writer(writer_id: int, session: AsyncSession):
    writer = await get_writer(writer_id=writer_id, session=session)
    if not writer:
        return False
    await session.delete(writer)
    await session.commit()
    return True


async def put_writer(writer_info: WriterID, session: AsyncSession):
    writer_id = writer_info.id
    writer_update = WriterIN(**writer_info.model_dump())
    writer = await get_writer(writer_id=writer_id, session=session)
    if not writer:
        return False

    for name, value in writer_update.model_dump().items():
        setattr(writer, name, value)
    await session.commit()
    return writer
