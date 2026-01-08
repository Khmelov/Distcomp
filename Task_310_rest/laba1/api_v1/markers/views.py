from fastapi import APIRouter, status, HTTPException
from .schemas import Marker, MarkerID
from loguru import logger
from api_v1.util import clear_storage


logger.add(
        sink = "app.log",
        mode="w",
        encoding="utf-8",
        format="{time} {level} {markers}",)


router = APIRouter()
prefix = "/markers"

current_marker = {
    "id": 0,
    "name": "",
}


@router.get(prefix + "/{get_id}",
            status_code=status.HTTP_200_OK,
            response_model=MarkerID)
async def marker_by_id(
    get_id: int
):
    global current_marker
    logger.info(f"GET marker by id: {get_id}")
    if current_marker["id"] != get_id:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such marker"
        )
    return MarkerID.model_validate(current_marker)


@router.get(prefix,
            status_code=status.HTTP_200_OK,
            response_model=MarkerID)
async def marker():
    global current_marker
    logger.info("GET marker")
    return MarkerID.model_validate(current_marker)




@router.post(prefix, 
             status_code=status.HTTP_201_CREATED,
             response_model=MarkerID)
async def create_marker(
    marker: Marker
):
    global current_marker
    logger.info(f"POST marker with body: {marker.model_dump()}")
    current_marker = {"id":0, **marker.model_dump() }

    return MarkerID.model_validate(current_marker)




@router.delete(prefix + "/{delete_id}",
               status_code=status.HTTP_204_NO_CONTENT)
async def delete_marker(
    delete_id: int
):
    global current_marker
    logger.info(f"DELETE marker with ID: {delete_id}")
    if current_marker["id"] != delete_id:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such marker")
    
    current_marker = clear_storage(current_marker)
    return 



@router.put(prefix,
            status_code=status.HTTP_200_OK,
            response_model=MarkerID)
async def put_writer(
    marker: MarkerID
):
    global current_marker
    logger.info(f"PUT marker with body: {marker.model_dump()}")
    # if comment. == 'x':
    #         raise HTTPException(
    #         status_code=status.HTTP_400_BAD_REQUEST,
    #         detail="Invlaid PUT data")
    current_marker = {**marker.model_dump()}
    return marker