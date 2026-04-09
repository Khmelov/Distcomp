from fastapi import APIRouter, Response, status

from ..dto.tag_dto import TagRequestTo, TagResponseTo
from ..services.tag_service import TagService

router = APIRouter(prefix="/api/v1.0/tags", tags=["tag"])
service = TagService()


@router.post("", response_model=TagResponseTo, status_code=status.HTTP_201_CREATED)
def create_tag(dto: TagRequestTo):
    return service.create(dto)


@router.get("", response_model=list[TagResponseTo], status_code=status.HTTP_200_OK)
def get_tags():
    return service.get_all()


@router.get("/{tag_id}", response_model=TagResponseTo, status_code=status.HTTP_200_OK)
def get_tag(tag_id: int):
    return service.get_by_id(tag_id)


@router.put("/{tag_id}", response_model=TagResponseTo, status_code=status.HTTP_200_OK)
def update_tag(tag_id: int, dto: TagRequestTo):
    return service.update(tag_id, dto)


@router.delete("/{tag_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_tag(tag_id: int):
    service.delete(tag_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)