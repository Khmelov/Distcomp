from fastapi import APIRouter
from starlette import status

from src.api.dependencies import MarkerServiceDep
from src.dto.marker import MarkerRequestTo, MarkerResponseTo

router = APIRouter(prefix="/markers", tags=["markers"])


@router.get("", response_model=list[MarkerResponseTo])
async def get_markers(service: MarkerServiceDep):
    return await service.get_all()


@router.get("/{marker_id}", response_model=MarkerResponseTo)
async def get_marker(marker_id: int, service: MarkerServiceDep):
    return await service.get_by_id(marker_id)


@router.post("", response_model=MarkerResponseTo, status_code=status.HTTP_201_CREATED)
async def create_marker(data: MarkerRequestTo, service: MarkerServiceDep):
    return await service.create(data)


@router.put("/{marker_id}", response_model=MarkerResponseTo)
async def update_marker(marker_id: int, data: MarkerRequestTo, service: MarkerServiceDep):
    return await service.update(marker_id, data)


@router.delete("/{marker_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_marker(marker_id: int, service: MarkerServiceDep):
    await service.delete(marker_id)
