from fastapi import APIRouter, status
from app.dto.mark import MarkRequestTo, MarkResponseTo
from app.services.mark_service import MarkService

router = APIRouter()
service = MarkService()


@router.post("/marks",
             response_model=MarkResponseTo,
             status_code=status.HTTP_201_CREATED)
def create_mark(dto: MarkRequestTo):
    return service.create(dto)


@router.get("/marks", response_model=list[MarkResponseTo])
def get_marks():
    return service.find_all()


@router.get("/marks/{id}", response_model=MarkResponseTo)
def get_mark(id: int):
    return service.find_by_id(id)


@router.put("/marks/{id}", response_model=MarkResponseTo)
def update_mark(id: int, dto: MarkRequestTo):
    return service.update(id, dto)


@router.delete("/marks/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_mark(id: int):
    service.delete(id)
