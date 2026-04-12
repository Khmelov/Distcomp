from fastapi import APIRouter
from starlette import status

from src.api.dependencies import EditorServiceDep
from src.dto.editor import EditorRequestTo, EditorResponseTo

router = APIRouter(prefix="/editors", tags=["editors"])


@router.get("", response_model=list[EditorResponseTo])
async def get_editors(service: EditorServiceDep):
    return await service.get_all()


@router.get("/{editor_id}", response_model=EditorResponseTo)
async def get_editor(editor_id: int, service: EditorServiceDep):
    return await service.get_by_id(editor_id)


@router.post("", response_model=EditorResponseTo, status_code=status.HTTP_201_CREATED)
async def create_editor(data: EditorRequestTo, service: EditorServiceDep):
    return await service.create(data)


@router.put("/{editor_id}", response_model=EditorResponseTo)
async def update_editor(editor_id: int, data: EditorRequestTo, service: EditorServiceDep):
    return await service.update(editor_id, data)


@router.delete("/{editor_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_editor(editor_id: int, service: EditorServiceDep):
    await service.delete(editor_id)
