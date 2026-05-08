from fastapi import APIRouter, status
from src.schemas.dto import EditorRequestTo, EditorResponseTo
from src.dependencies.services import EditorServiceDep


router = APIRouter(
    prefix="/editors",
)


@router.post(
    path="",
    response_model=EditorResponseTo,
    status_code=status.HTTP_201_CREATED,
)
async def create_editor(editor_in: EditorRequestTo, editor_service: EditorServiceDep):
    return await editor_service.create(editor_in)


@router.get(
    path="",
    response_model=list[EditorResponseTo],
    status_code=status.HTTP_200_OK,
)
async def get_editors(editor_service: EditorServiceDep):
    return await editor_service.get_all()


@router.get(
    path="/{id}",
    response_model=EditorResponseTo,
    status_code=status.HTTP_200_OK,
)
async def get_editor(id: int, editor_service: EditorServiceDep):
    return await editor_service.get_by_id(id)


@router.put(
    path="/{id}",
    response_model=EditorResponseTo,
    status_code=status.HTTP_200_OK,
)
async def update_editor(id: int, editor_in: EditorRequestTo, editor_service: EditorServiceDep):
    return await editor_service.update(id, editor_in)


@router.delete(path="/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_editor(id: int, editor_service: EditorServiceDep):
    await editor_service.delete(id)
