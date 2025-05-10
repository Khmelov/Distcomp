from fastapi import APIRouter, status, HTTPException, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from loguru import logger

from db_helper import db_helper

from .schemas import Sticker, StickerID
import api_v1.stickers.crud as crud


logger.add(
    sink="RV2Lab.log",
    mode="w",
    encoding="utf-8",
    format="{level} {sticker}",
)


router = APIRouter(
    prefix="/stickers",
)

costyl_id = 0


@router.get("/{get_id}", status_code=status.HTTP_200_OK, response_model=StickerID)
async def sticker_by_id(
    get_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"GET definite Sticker with id: {get_id}")
    sticker = await crud.get_sticker(session=session, sticker_id=get_id)
    if not sticker:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such Sticker"
        )
    return sticker


@router.get("", status_code=status.HTTP_200_OK, response_model=StickerID)
async def sticker(session: AsyncSession = Depends(db_helper.session_dependency)):
    logger.info("GET Sticker")
    global costyl_id

    sticker = await crud.get_sticker(session=session, sticker_id=costyl_id)
    if not sticker:
        return {
            "id": 0,
            "name": " "*2,
        }
    return sticker


@router.post("", status_code=status.HTTP_201_CREATED, response_model=StickerID)
async def create_sticker(
    sticker_info: Sticker, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"POST Sticker with body: {sticker_info.model_dump()}")
    sticker = await crud.create_sticker(session=session, sticker_info=sticker_info)
    if not sticker:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Incorrect data"
        )

    global costyl_id
    costyl_id = sticker.id
    return sticker


@router.delete("/{delete_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_sticker(
    delete_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):

    logger.info(f"DELETE Sticker with ID: {delete_id}")
    delete_state = await crud.delete_sticker(sticker_id=delete_id, session=session)
    if not delete_state:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such Sticker"
        )
    return


@router.put("", status_code=status.HTTP_200_OK, response_model=StickerID)
async def put_sticker(
    sticker: StickerID, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"PUT Sticker with body: {sticker.model_dump()}")
    sticker = await crud.put_sticker(sticker_info=sticker, session=session)
    if not sticker:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Invlaid PUT data"
        )
    return sticker
