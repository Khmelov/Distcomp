from http import HTTPStatus
from typing import List

from fastapi import APIRouter
from fastapi.params import Depends

from src.deps.base import get_label_service
from src.schemas.label import LabelResponseTo, LabelRequestTo
from src.services import LabelService

router = APIRouter(prefix="/labels")

@router.get("", response_model=List[LabelResponseTo], status_code=HTTPStatus.OK)
async def get_all(service: LabelService = Depends(get_label_service)):
    return await service.get_all()

@router.get("/{label_id}", response_model=LabelResponseTo, status_code=HTTPStatus.OK)
async def get_by_id(label_id: int, service: LabelService = Depends(get_label_service)):
    return await service.get_one(label_id)

@router.post("", response_model=LabelResponseTo, status_code=HTTPStatus.CREATED)
async def create(dto: LabelRequestTo, service: LabelService = Depends(get_label_service)):
    return await service.create(dto)

@router.put("/{label_id}", response_model=LabelResponseTo, status_code=HTTPStatus.OK)
async def update(label_id: int, dto: LabelRequestTo, service: LabelService = Depends(get_label_service)):
    return await service.update(label_id, dto)

@router.delete("/{label_id}", status_code=HTTPStatus.NO_CONTENT)
async def delete(label_id: int, service: LabelService = Depends(get_label_service)):
    await service.delete(label_id)