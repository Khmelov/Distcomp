from fastapi import APIRouter, HTTPException
from starlette import status

from src.api.dependencies import AuthServiceDep, EditorServiceDep
from src.api.v2.deps import CurrentUserDep, OptionalUserDep, ensure_editor_mutate_self_or_admin
from src.dto.editor import EditorOut, EditorRegisterV2, EditorRequestTo
from src.models.user_role import UserRole

router = APIRouter(prefix="/editors", tags=["editors-v2"])


@router.post("", response_model=EditorOut, status_code=status.HTTP_201_CREATED)
async def create_editor_v2(
    data: EditorRegisterV2,
    service: AuthServiceDep,
    optional_user: OptionalUserDep,
) -> EditorOut:
    if optional_user is None:
        return await service.register(data)
    if optional_user.role == UserRole.ADMIN:
        return await service.register(data)
    raise HTTPException(status_code=403, detail="Only administrators may create editors when authenticated")


@router.get("", response_model=list[EditorOut])
async def list_editors(service: EditorServiceDep, user: CurrentUserDep):
    return await service.get_all()


@router.get("/{editor_id}", response_model=EditorOut)
async def get_editor(editor_id: int, service: EditorServiceDep, user: CurrentUserDep):
    return await service.get_by_id(editor_id)


@router.put("/{editor_id}", response_model=EditorOut)
async def update_editor_v2(
    editor_id: int,
    data: EditorRequestTo,
    service: EditorServiceDep,
    user: CurrentUserDep,
) -> EditorOut:
    ensure_editor_mutate_self_or_admin(user, editor_id)
    if user.role == UserRole.CUSTOMER:
        if data.role is not None and data.role != UserRole.CUSTOMER:
            raise HTTPException(status_code=403, detail="Cannot change role")
    return await service.update(editor_id, data)


@router.delete("/{editor_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_editor_v2(editor_id: int, service: EditorServiceDep, user: CurrentUserDep):
    ensure_editor_mutate_self_or_admin(user, editor_id)
    await service.delete(editor_id)
