from fastapi import APIRouter, status, HTTPException, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from loguru import logger

from db_helper import db_helper

from .schemas import Editor, EditorID
import api_v1.editors.crud as crud
from .helpers import check_login


logger.add(
    sink="RV2Lab.log",
    mode="w",
    encoding="utf-8",
    format="{level} {comment}",
)

router = APIRouter(
    prefix="/editors",
)

costyl_id = 0


@router.get("/{editor_id}", status_code=status.HTTP_200_OK, response_model=EditorID)
async def editor_by_id(
    editor_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"GET definite Editor with ID: {editor_id}")
    editor = await crud.get_editor(session=session, editor_id=editor_id)
    if not editor:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such editor"
        )
    return editor


@router.get("", status_code=status.HTTP_200_OK, response_model=EditorID)
async def editor(session: AsyncSession = Depends(db_helper.session_dependency)):
    global costyl_id
    logger.info("GET Editor")
    editor = await crud.get_editor(editor_id=costyl_id, session=session)
    if not editor:
        editor = {
            "id": 0,
            "login": " " * 2,
            "password": " " * 8,
            "firstname": " " * 2,
            "lastname": " " * 2,
        }
    return editor


@router.post("", status_code=status.HTTP_201_CREATED, response_model=EditorID)
async def create_editor(
    editor_info: Editor, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"POST Сreator with body: {editor_info.model_dump()}")

    await check_login(login=editor_info.login, session=session)

    editor = await crud.create_editor(session=session, editor_info=editor_info)
    if not editor:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Incorrect data"
        )

    global costyl_id
    costyl_id = editor.id
    return editor


@router.delete("/{delete_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_editor(
    delete_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"DELETE Сreator with ID: {delete_id}")
    delete_state = await crud.delete_editor(editor_id=delete_id, session=session)
    if not delete_state:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such Editor"
        )
    return


@router.put("", status_code=status.HTTP_200_OK, response_model=EditorID)
async def put_editor(
    editor_info: EditorID,
    session: AsyncSession = Depends(db_helper.session_dependency),
):
    logger.info(f"PUT Сreator with body: {editor_info.model_dump()}")

    await check_login(login=editor_info.login, session=session)

    editor = await crud.put_editor(editor_info=editor_info, session=session)
    if not editor:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Invlaid PUT data"
        )
    return editor
