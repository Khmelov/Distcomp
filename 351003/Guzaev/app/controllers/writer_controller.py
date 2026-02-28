from fastapi import APIRouter, status, Response
from typing import List
from app.dtos.writer_dto import WriterRequestTo, WriterResponseTo
from app.services.writer_service import WriterService

router = APIRouter(prefix="/api/v1.0/writers", tags=["writers"])
service = WriterService()

@router.post("", status_code=status.HTTP_201_CREATED, response_model=WriterResponseTo)
def create_writer(dto: WriterRequestTo):
    return service.create(dto)

@router.get("", response_model=List[WriterResponseTo])
def get_writers():
    return service.get_all()

@router.get("/{id}", response_model=WriterResponseTo)
def get_writer(id: int):
    return service.get_by_id(id)

@router.put("/{id}", response_model=WriterResponseTo)
def update_writer(id: int, dto: WriterRequestTo):
    return service.update(id, dto)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_writer(id: int):
    service.delete(id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
