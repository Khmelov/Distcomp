from fastapi import APIRouter, status, HTTPException
from .schemas import Editor, EditorID
from loguru import logger
from api_v1.util import clear_storage

logger.add(
        sink = "RV1Lab.log",
        mode="w",
        encoding="utf-8",
        format="{time} {level} {comment}",)

router = APIRouter()

prefix = "/editors"

current_user = {
    "id": 0,
    "login": "",
    "password": "",
    "firstname": "",
    "lastname": "",
}



@router.get(prefix + "/{editor_id}",
            status_code=status.HTTP_200_OK,
            response_model=EditorID)
async def editor_by_id(
    editor_id: int
):
    global current_user
    logger.info(f"GET definite editor with ID: {editor_id}")

    if current_user["id"] != editor_id:
        return HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such Editor")
    return EditorID.model_validate(current_user)



@router.get(prefix,
            status_code=status.HTTP_200_OK,
            response_model=EditorID)
async def editors():
    logger.info("GET editor")
    return EditorID.model_validate(current_user)



@router.post(prefix, 
             status_code=status.HTTP_201_CREATED,
             response_model=EditorID)
async def create_editor(
    editor: Editor
):
    global current_user
    logger.info(f"POST editor with body: {editor.model_dump()}")
    current_user = {"id":0, **editor.model_dump() }

    return EditorID.model_validate(current_user)



@router.delete(prefix + "/{delete_id}",
               status_code=status.HTTP_204_NO_CONTENT)
async def delete_editor(
    delete_id: int
):
    global current_user
    logger.info(f"DELETE editor with ID: {delete_id}")
    if current_user["id"] != delete_id:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such Editor")
    
    current_user = clear_storage(current_user)
    return 



@router.put(prefix,
            status_code=status.HTTP_200_OK,
            response_model=EditorID)
async def put_editor(
    editor: EditorID
):
    global current_user
    logger.info(f"PUT editor with body: {editor.model_dump()}")
    if editor.login == 'x':
            raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invlaid PUT data")

    current_user = {**editor.model_dump()}
    return editor
