from fastapi import APIRouter, status, HTTPException
from .schemas import Message, MessageID
from loguru import logger
from api_v1.util import clear_storage

logger.add(
        sink = "RV1Lab.log",
        mode="w",
        encoding="utf-8",
        format="{time} {level} {message}",)

router = APIRouter()
prefix = "/messages"


current_message = {
    "id": 0,
    "storyId": 0,
    "content": "",
}


@router.get(prefix + "/{get_id}",
            status_code=status.HTTP_200_OK,
            response_model=MessageID)
async def message_by_id(
    get_id: int
):
    global current_message
    logger.info(f"GET message by id: {get_id}")
    if current_message["id"] != get_id:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such message"
        )
    return MessageID.model_validate(current_message)


@router.get(prefix,
            status_code=status.HTTP_200_OK,
            response_model=MessageID)
async def message():
    global current_message
    logger.info("GET message")
    return MessageID.model_validate(current_message)



@router.post(prefix, 
             status_code=status.HTTP_201_CREATED,
             response_model=MessageID)
async def create_message(
    message: Message
):
    global current_message
    logger.info(f"POST message with body: {message.model_dump()}")
    current_message = {"id":0, **message.model_dump() }

    return MessageID.model_validate(current_message)




@router.delete(prefix + "/{delete_id}",
               status_code=status.HTTP_204_NO_CONTENT)
async def delete_message(
    delete_id: int
):
    global current_message
    logger.info(f"DELETE message with ID: {delete_id}")
    if current_message["id"] != delete_id:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such message")

    
    current_message = clear_storage(current_message)
    current_message["storyId"] = 100000
    return 



@router.put(prefix,
            status_code=status.HTTP_200_OK,
            response_model=MessageID)
async def put_creator(
    message: MessageID
):
    global current_message
    logger.info(f"PUT message with body: {message.model_dump()}")
    # if message. == 'x':
    #         raise HTTPException(
    #         status_code=status.HTTP_400_BAD_REQUEST,
    #         detail="Invlaid PUT data")

    current_message = {**message.model_dump()}
    return message