from fastapi import APIRouter, status, HTTPException, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from loguru import logger

from db_helper import db_helper

from .schemas import Writer, WriterID
import api_v1.writers.crud as crud
from .helpers import check_login


logger.add(
    sink="app.log",
    mode="w",
    encoding="utf-8",
    format="{time} {level} {writers}",
)

router = APIRouter(
    prefix="/writers",
)

costyl_id = 0


@router.get("/{writer_id}", status_code=status.HTTP_200_OK, response_model=WriterID)
async def writer_by_id(
    writer_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"GET definite Writer with ID: {writer_id}")
    writer = await crud.get_writer(session=session, writer_id=writer_id)
    if not writer:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such writer"
        )
    return writer


@router.get("", status_code=status.HTTP_200_OK, response_model=WriterID)
async def writer(session: AsyncSession = Depends(db_helper.session_dependency)):
    global costyl_id
    logger.info("GET Writer")
    writer = await crud.get_writer(writer_id=costyl_id, session=session)
    if not writer:
        writer = {
            "id": 0,
            "login": " " * 2,
            "password": " " * 8,
            "firstname": " " * 2,
            "lastname": " " * 2,
        }
    return writer


@router.post("", status_code=status.HTTP_201_CREATED, response_model=WriterID)
async def create_writer(
    writer_info: Writer, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"POST Сreator with body: {writer_info.model_dump()}")

    await check_login(login=writer_info.login, session=session)

    writer = await crud.create_writer(session=session, writer_info=writer_info)
    if not writer:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Incorrect data"
        )

    global costyl_id
    costyl_id = writer.id
    return writer


@router.delete("/{delete_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_writer(
    delete_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"DELETE Сreator with ID: {delete_id}")
    delete_state = await crud.delete_writer(writer_id=delete_id, session=session)
    if not delete_state:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such Writer"
        )
    return


@router.put("", status_code=status.HTTP_200_OK, response_model=WriterID)
async def put_writer(
    writer_info: WriterID,
    session: AsyncSession = Depends(db_helper.session_dependency),
):
    logger.info(f"PUT Сreator with body: {writer_info.model_dump()}")

    await check_login(login=writer_info.login, session=session)

    writer = await crud.put_writer(writer_info=writer_info, session=session)
    if not writer:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Invlaid PUT data"
        )
    return writer
