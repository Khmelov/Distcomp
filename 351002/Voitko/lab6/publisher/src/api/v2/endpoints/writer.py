from http import HTTPStatus
from typing import List

from fastapi import APIRouter, Depends

from src.deps.base import get_writer_service
from src.schemas.writer import WriterResponseTo, WriterRequestTo
from src.services.writer import WriterService

public_router = APIRouter(prefix="/writers")

@public_router.post("", response_model=WriterResponseTo, status_code=HTTPStatus.CREATED)
async def create(request: WriterRequestTo, service: WriterService = Depends(get_writer_service)):
    return await service.create(request)


router = APIRouter(prefix="/writers")

@router.get("", response_model=List[WriterResponseTo], status_code=HTTPStatus.OK)
async def get_all(service: WriterService = Depends(get_writer_service)):
    return await service.get_all()

@router.get("/{writer_id}", response_model=WriterResponseTo, status_code=HTTPStatus.OK)
async def get_by_id(writer_id: int, service: WriterService = Depends(get_writer_service)):
    return await service.get_one(writer_id)

@router.put("/{writer_id}", response_model=WriterResponseTo, status_code=HTTPStatus.OK)
async def update(writer_id: int, dto: WriterRequestTo, service: WriterService = Depends(get_writer_service)):
    return await service.update(writer_id, dto)

@router.delete("/{writer_id}", status_code=HTTPStatus.NO_CONTENT)
async def delete(writer_id: int, service: WriterService = Depends(get_writer_service)):
    await service.delete(writer_id)