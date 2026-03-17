from fastapi import APIRouter, status
from app.schemas.label import LabelRequestTo, LabelResponseTo
from app.services.label_service import LabelService
from typing import List

router = APIRouter()
service = LabelService()

@router.post("", response_model=LabelResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: LabelRequestTo):
    return service.create(dto)

@router.get("", response_model=List[LabelResponseTo])
async def get_all():
    return service.get_all()

@router.get("/{id}", response_model=LabelResponseTo)
async def get_by_id(id: int):
    return service.get_by_id(id)

@router.put("/{id}", response_model=LabelResponseTo)
async def update(id: int, dto: LabelRequestTo):
    return service.update(id, dto)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int):
    service.delete(id)