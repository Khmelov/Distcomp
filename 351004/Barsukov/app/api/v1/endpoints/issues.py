from fastapi import APIRouter, status, Body
from app.schemas.issue import IssueRequestTo, IssueResponseTo
from app.services.issue_service import IssueService
from typing import List

router = APIRouter()
service = IssueService()

@router.post("", response_model=IssueResponseTo, status_code=status.HTTP_201_CREATED)
async def create(dto: IssueRequestTo = Body(...)):
    return service.create(dto)

@router.get("", response_model=List[IssueResponseTo])
async def get_all():
    return service.get_all()

@router.get("/{id}", response_model=IssueResponseTo)
async def get_by_id(id: int):
    return service.get_by_id(id)

@router.put("/{id}", response_model=IssueResponseTo)
async def update(id: int, dto: IssueRequestTo = Body(...)):
    return service.update(id, dto)

@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete(id: int):
    service.delete(id)