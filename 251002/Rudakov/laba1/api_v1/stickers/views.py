from fastapi import APIRouter, status, HTTPException
from .schemas import Sticker, StickerID
from loguru import logger
from api_v1.util import clear_storage


logger.add(
        sink = "RV1Lab.log",
        mode="w",
        encoding="utf-8",
        format="{time} {level} {message}",)


router = APIRouter()
prefix = "/stickers"

current_sticker = {
    "id": 0,
    "name": "",
}


@router.get(prefix + "/{get_id}",
            status_code=status.HTTP_200_OK,
            response_model=StickerID)
async def sticker_by_id(
    get_id: int
):
    global current_sticker
    logger.info(f"GET sticker by id: {get_id}")
    if current_sticker["id"] != get_id:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such sticker"
        )
    return StickerID.model_validate(current_sticker)


@router.get(prefix,
            status_code=status.HTTP_200_OK,
            response_model=StickerID)
async def sticker():
    global current_sticker
    logger.info("GET sticker")
    return StickerID.model_validate(current_sticker)




@router.post(prefix, 
             status_code=status.HTTP_201_CREATED,
             response_model=StickerID)
async def create_sticker(
    sticker: Sticker
):
    global current_sticker
    logger.info(f"POST sticker with body: {sticker.model_dump()}")
    current_sticker = {"id":0, **sticker.model_dump() }

    return StickerID.model_validate(current_sticker)




@router.delete(prefix + "/{delete_id}",
               status_code=status.HTTP_204_NO_CONTENT)
async def delete_sticker(
    delete_id: int
):
    global current_sticker
    logger.info(f"DELETE sticker with ID: {delete_id}")
    if current_sticker["id"] != delete_id:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such sticker")
    
    current_sticker = clear_storage(current_sticker)
    return 



@router.put(prefix,
            status_code=status.HTTP_200_OK,
            response_model=StickerID)
async def put_creator(
    sticker: StickerID
):
    global current_sticker
    logger.info(f"PUT sticker with body: {sticker.model_dump()}")
    # if message. == 'x':
    #         raise HTTPException(
    #         status_code=status.HTTP_400_BAD_REQUEST,
    #         detail="Invlaid PUT data")
    current_sticker = {**sticker.model_dump()}
    return sticker