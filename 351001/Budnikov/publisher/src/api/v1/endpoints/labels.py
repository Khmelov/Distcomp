from fastapi import APIRouter, status
from src.schemas.dto import LabelRequestTo, LabelResponseTo
from src.dependencies.services import LabelServiceDep


router = APIRouter(prefix="/labels")


@router.post(path="", response_model=LabelResponseTo, status_code=status.HTTP_201_CREATED)
async def create_label(label_in: LabelRequestTo, label_service: LabelServiceDep):
    return await label_service.create(label_in)


@router.get(path="", response_model=list[LabelResponseTo], status_code=status.HTTP_200_OK)
async def get_labels(label_service: LabelServiceDep):
    return await label_service.get_all()


@router.get(path="/{id}", response_model=LabelResponseTo, status_code=status.HTTP_200_OK)
async def get_label(id: int, label_service: LabelServiceDep):
    return await label_service.get_by_id(id)


@router.put(path="/{id}", response_model=LabelResponseTo, status_code=status.HTTP_200_OK)
async def update_label(id: int, label_in: LabelRequestTo, label_service: LabelServiceDep):
    return await label_service.update(id, label_in)


@router.delete(path="/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_label(id: int, label_service: LabelServiceDep):
    await label_service.delete(id)
