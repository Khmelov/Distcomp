from fastapi import APIRouter, status, HTTPException
from .schemas import Creator, CreatorID
from loguru import logger
from api_v1.util import clear_storage

logger.add(
        sink = "RV1Lab.log",
        mode="w",
        encoding="utf-8",
        format="{time} {level} {message}",)

router = APIRouter()

prefix = "/creators"

current_user = {
    "id": 0,
    "login": "",
    "password": "",
    "firstname": "",
    "lastname": "",
}



@router.get(prefix + "/{creator_id}",
            status_code=status.HTTP_200_OK,
            response_model=CreatorID)
async def creator_by_id(
    creator_id: int
):
    global current_user
    logger.info(f"GET definite creator with ID: {creator_id}")

    if current_user["id"] != creator_id:
        return HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such Creator")
    return CreatorID.model_validate(current_user)



@router.get(prefix,
            status_code=status.HTTP_200_OK,
            response_model=CreatorID)
async def creators():
    logger.info("GET creator")
    return CreatorID.model_validate(current_user)



@router.post(prefix, 
             status_code=status.HTTP_201_CREATED,
             response_model=CreatorID)
async def create_creator(
    creator: Creator
):
    global current_user
    logger.info(f"POST creator with body: {creator.model_dump()}")
    current_user = {"id":0, **creator.model_dump() }

    return CreatorID.model_validate(current_user)



@router.delete(prefix + "/{delete_id}",
               status_code=status.HTTP_204_NO_CONTENT)
async def delete_creator(
    delete_id: int
):
    global current_user
    logger.info(f"DELETE creator with ID: {delete_id}")
    if current_user["id"] != delete_id:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such Creator")
    
    current_user = clear_storage(current_user)
    return 



@router.put(prefix,
            status_code=status.HTTP_200_OK,
            response_model=CreatorID)
async def put_creator(
    creator: CreatorID
):
    global current_user
    logger.info(f"PUT creator with body: {creator.model_dump()}")
    if creator.login == 'x':
            raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invlaid PUT data")

    current_user = {**creator.model_dump()}
    return creator
