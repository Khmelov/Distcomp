from fastapi import APIRouter
from starlette import status

from src.api.dependencies import MarkerServiceDep
from src.api.v2.deps import CurrentUserDep, ensure_admin
from src.dto.marker import MarkerRequestTo, MarkerResponseTo

router = APIRouter(prefix="/markers", tags=["markers-v2"])


@router.get("", response_model=list[MarkerResponseTo])
async def list_markers(service: MarkerServiceDep, user: CurrentUserDep):
    return await service.get_all()


@router.get("/{marker_id}", response_model=MarkerResponseTo)
async def get_marker(marker_id: int, service: MarkerServiceDep, user: CurrentUserDep):
    return await service.get_by_id(marker_id)


@router.post("", response_model=MarkerResponseTo, status_code=status.HTTP_201_CREATED)
async def create_marker_v2(data: MarkerRequestTo, service: MarkerServiceDep, user: CurrentUserDep):
    ensure_admin(user)
    return await service.create(data)


@router.put("/{marker_id}", response_model=MarkerResponseTo)
async def update_marker_v2(
    marker_id: int,
    data: MarkerRequestTo,
    service: MarkerServiceDep,
    user: CurrentUserDep,
):
    ensure_admin(user)
    return await service.update(marker_id, data)


@router.delete("/{marker_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_marker_v2(marker_id: int, service: MarkerServiceDep, user: CurrentUserDep):
    ensure_admin(user)
    await service.delete(marker_id)
