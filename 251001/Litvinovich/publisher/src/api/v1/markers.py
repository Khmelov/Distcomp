from fastapi import APIRouter, Depends
from typing import List, Annotated

from src.api.v1.dependencies import get_markers_service
from src.schemas.markers import MarkerResponseTo, MarkerRequestToUpdate
from src.schemas.markers import MarkerRequestToAdd
from src.services.markers import MarkersService

router = APIRouter(prefix="/markers")


@router.get("", response_model=List[MarkerResponseTo])
async def get_markers(markers_service: Annotated[MarkersService, Depends(get_markers_service)]):
    return await markers_service.get_markers()


@router.get("/{marker_id}", response_model=MarkerResponseTo)
async def get_marker_by_id(marker_id: int,
                            markers_service: Annotated[MarkersService, Depends(get_markers_service)]):
    return await markers_service.get_marker_by_id(marker_id)


@router.post("", response_model=MarkerResponseTo, status_code=201)
async def create_marker(marker: MarkerRequestToAdd,
                         markers_service: Annotated[MarkersService, Depends(get_markers_service)]):
    marker_response = await markers_service.create_marker(marker)
    return marker_response


@router.delete("/{marker_id}", status_code=204)
async def delete_marker(marker_id: int,
                         markers_service: Annotated[MarkersService, Depends(get_markers_service)]):
    await markers_service.delete_marker(marker_id)
    return marker_id


@router.put("", response_model=MarkerResponseTo)
async def update_marker(marker: MarkerRequestToUpdate,
                         markers_service: Annotated[MarkersService, Depends(get_markers_service)]):
    return await markers_service.update_marker(marker)

