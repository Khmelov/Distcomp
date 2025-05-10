from fastapi import APIRouter, status, HTTPException, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from loguru import logger

from db_helper import db_helper

from .schemas import Creator, CreatorID
import api_v1.creators.crud as crud
from .helpers import check_login


logger.add(
    sink="RV2Lab.log",
    mode="w",
    encoding="utf-8",
    format="{level} {message}",
)

router = APIRouter(
    prefix="/creators",
)

costyl_id = 0


@router.get("/{creator_id}", status_code=status.HTTP_200_OK, response_model=CreatorID)
async def creator_by_id(
    creator_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"GET definite Creator with ID: {creator_id}")
    creator = await crud.get_creator(session=session, creator_id=creator_id)
    if not creator:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such creator"
        )
    return creator


@router.get("", status_code=status.HTTP_200_OK, response_model=CreatorID)
async def creator(session: AsyncSession = Depends(db_helper.session_dependency)):
    global costyl_id
    logger.info("GET Creator")
    creator = await crud.get_creator(creator_id=costyl_id, session=session)
    if not creator:
        creator = {
            "id": 0,
            "login": " " * 2,
            "password": " " * 8,
            "firstname": " " * 2,
            "lastname": " " * 2,
        }
    return creator


@router.post("", status_code=status.HTTP_201_CREATED, response_model=CreatorID)
async def create_creator(
    creator_info: Creator, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"POST Сreator with body: {creator_info.model_dump()}")

    await check_login(login=creator_info.login, session=session)

    creator = await crud.create_creator(session=session, creator_info=creator_info)
    if not creator:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Incorrect data"
        )

    global costyl_id
    costyl_id = creator.id
    return creator


@router.delete("/{delete_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_creator(
    delete_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"DELETE Сreator with ID: {delete_id}")
    delete_state = await crud.delete_creator(creator_id=delete_id, session=session)
    if not delete_state:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such Creator"
        )
    return


@router.put("", status_code=status.HTTP_200_OK, response_model=CreatorID)
async def put_creator(
    creator_info: CreatorID,
    session: AsyncSession = Depends(db_helper.session_dependency),
):
    logger.info(f"PUT Сreator with body: {creator_info.model_dump()}")

    await check_login(login=creator_info.login, session=session)

    creator = await crud.put_creator(creator_info=creator_info, session=session)
    if not creator:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Invlaid PUT data"
        )
    return creator
