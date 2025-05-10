from fastapi import APIRouter, status, HTTPException, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from loguru import logger

from db_helper import db_helper

from .schemas import Message, MessageID
import api_v1.messages.crud as crud
from .helper import check_story


logger.add(
    sink="RV2Lab.log",
    mode="w",
    encoding="utf-8",
    format="{level} {message}",
)

router = APIRouter(
    prefix="/messages",
)

global costyl_id
costyl_id = 0


@router.get("/{get_id}", status_code=status.HTTP_200_OK, response_model=MessageID)
async def message_by_id(
    get_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"GET definite Message with id: {get_id}")
    message = await crud.get_message(session=session, message_id=get_id)
    if not message:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such message"
        )
    return message


@router.get("", status_code=status.HTTP_200_OK, response_model=MessageID)
async def message(session: AsyncSession = Depends(db_helper.session_dependency)):
    logger.info("GET Message")
    global costyl_id

    message = await crud.get_message(session=session, message_id=costyl_id)
    if not message:
        return {
            "id": 0,
            "storyId": 0,
            "content": " " * 2,
        }
    return message


@router.post("", status_code=status.HTTP_201_CREATED, response_model=MessageID)
async def create_message(
    message_info: Message, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"POST Message with body: {message_info.model_dump()}")

    await check_story(session=session, story_id=message_info.storyId)

    message = await crud.create_message(session=session, message_info=message_info)

    if not message:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Incorrect data"
        )

    global costyl_id
    costyl_id = message.id
    return message


@router.delete("/{delete_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_message(
    delete_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):

    logger.info(f"DELETE message with ID: {delete_id}")
    delete_state = await crud.delete_message(message_id=delete_id, session=session)
    if not delete_state:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such Message"
        )
    return


@router.put("", status_code=status.HTTP_200_OK, response_model=MessageID)
async def put_creator(
    message: MessageID, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"PUT Message with body: {message.model_dump()}")
    await check_story(session=session, story_id=message.storyId)
    message = await crud.put_message(message_info=message, session=session)
    if not message:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Invlaid PUT data"
        )
    return message
