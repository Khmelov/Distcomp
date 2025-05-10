from sqlalchemy import Result, select
from .schemas import Message as MessageIN, MessageID
from sqlalchemy.ext.asyncio import AsyncSession
from models import Message



async def create_message(
        message_info: MessageIN,
        session: AsyncSession
):
    message = Message(**message_info.model_dump())
    session.add(message)
    await session.commit()
    return message



async def get_message(
        session: AsyncSession,      
        message_id: int
):
    stat = select(Message).where(Message.id == message_id)
    result: Result = await session.execute(stat)
    # creators: Sequence = result.scalars().all()
    message: Message | None = result.scalar_one_or_none()
    return message


# async def get_message_by_title(
#         session: AsyncSession,      
#         message_title: str
# ):
#     stat = select(message).where(message.title == message_title)
#     result: Result = await session.execute(stat)
#     # creators: Sequence = result.scalars().all()
#     message: message | None = result.scalar_one_or_none()

#     return message


async def delete_message(
        message_id: int,
        session: AsyncSession
):
    message = await get_message(message_id=message_id, session=session)
    if not message:
        return False
    await session.delete(message)
    await session.commit()
    return True


async def put_message(
        message_info: MessageID,
        session: AsyncSession
):
    message_id = message_info.id
    message_update = MessageIN(**message_info.model_dump())
    message = await get_message(message_id=message_id, session=session)
    if not message:
        return False
    
    for name, value in message_update.model_dump().items():
        setattr(message, name, value)
    await session.commit()
    return message