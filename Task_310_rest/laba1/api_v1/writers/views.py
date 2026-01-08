from fastapi import APIRouter, status, HTTPException
from .schemas import Writer, WriterID
from loguru import logger
from api_v1.util import clear_storage

logger.add(
        sink = "app.log",
        mode="w",
        encoding="utf-8",
        format="{time} {level} {writers}",)

router = APIRouter()

prefix = "/writers"

current_writer = {
    "id": 0,
    "login": "",
    "password": "",
    "firstname": "",
    "lastname": "",
}



@router.get(prefix + "/{writer_id}",
            status_code=status.HTTP_200_OK,
            response_model=WriterID)
async def writer_by_id(
    writer_id: int
):
    global current_writer
    logger.info(f"GET definite writer with ID: {writer_id}")

    if current_writer["id"] != writer_id:
        return HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such Writer")
    return WriterID.model_validate(current_writer)



@router.get(prefix,
            status_code=status.HTTP_200_OK,
            response_model=WriterID)
async def writers():
    logger.info("GET writer")
    return WriterID.model_validate(current_writer)



@router.post(prefix, 
             status_code=status.HTTP_201_CREATED,
             response_model=WriterID)
async def create_writer(
    writer: Writer
):
    global current_writer
    logger.info(f"POST writer with body: {writer.model_dump()}")
    current_writer = {"id":0, **writer.model_dump() }

    return WriterID.model_validate(current_writer)



@router.delete(prefix + "/{delete_id}",
               status_code=status.HTTP_204_NO_CONTENT)
async def delete_writer(
    delete_id: int
):
    global current_writer
    logger.info(f"DELETE writer with ID: {delete_id}")
    if current_writer["id"] != delete_id:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such Writer")
    
    current_writer = clear_storage(current_writer)
    return 



@router.put(prefix,
            status_code=status.HTTP_200_OK,
            response_model=WriterID)
async def put_writer(
    writer: WriterID
):
    global current_writer
    logger.info(f"PUT writer with body: {writer.model_dump()}")
    if writer.login == 'x':
            raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invlaid PUT data")

    current_writer = {**writer.model_dump()}
    return writer
