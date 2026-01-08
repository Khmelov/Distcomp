from fastapi import APIRouter, status, HTTPException, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from loguru import logger

from db_helper import db_helper

from .schemas import Marker, MarkerID
import api_v1.markers.crud as crud


logger.add(
    sink="app.log",
    mode="w",
    encoding="utf-8",
    format="{time} {level} {markers}",
)


router = APIRouter(
    prefix="/markers",
)

costyl_id = 0


@router.get("/{get_id}", status_code=status.HTTP_200_OK, response_model=MarkerID)
async def marker_by_id(
    get_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"GET definite Marker with id: {get_id}")
    marker = await crud.get_marker(session=session, marker_id=get_id)
    if not marker:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such Marker"
        )
    return marker


@router.get("", status_code=status.HTTP_200_OK, response_model=MarkerID)
async def marker(session: AsyncSession = Depends(db_helper.session_dependency)):
    logger.info("GET Marker")
    global costyl_id

    marker = await crud.get_marker(session=session, marker_id=costyl_id)
    if not marker:
        return {
            "id": 0,
            "name": " "*2,
        }
    return marker


@router.post("", status_code=status.HTTP_201_CREATED, response_model=MarkerID)
async def create_marker(
    marker_info: Marker, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"POST Marker with body: {marker_info.model_dump()}")
    marker = await crud.create_marker(session=session, marker_info=marker_info)
    if not marker:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Incorrect data"
        )

    global costyl_id
    costyl_id = marker.id
    return marker


@router.delete("/{delete_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_marker(
    delete_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):

    logger.info(f"DELETE Marker with ID: {delete_id}")
    delete_state = await crud.delete_marker(marker_id=delete_id, session=session)
    if not delete_state:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such Marker"
        )
    return


@router.put("", status_code=status.HTTP_200_OK, response_model=MarkerID)
async def put_marker(
    marker: MarkerID, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"PUT Marker with body: {marker.model_dump()}")
    marker = await crud.put_marker(marker_info=marker, session=session)
    if not marker:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Invlaid PUT data"
        )
    return marker
