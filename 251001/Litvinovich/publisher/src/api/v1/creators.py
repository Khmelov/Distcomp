from fastapi import APIRouter, Depends
from typing import List, Annotated

from src.api.v1.dependencies import get_creators_service
from src.schemas.creators import CreatorResponseTo, CreatorRequestToUpdate
from src.schemas.creators import CreatorRequestToAdd
from src.services.creators import CreatorsService

router = APIRouter(prefix="/creators")


@router.get("", response_model=List[CreatorResponseTo])
async def get_creators(creators_service: Annotated[CreatorsService, Depends(get_creators_service)]):
    return await creators_service.get_creators()


@router.get("/{creator_id}", response_model=CreatorResponseTo)
async def get_creator_by_id(creator_id: int,
                            creators_service: Annotated[CreatorsService, Depends(get_creators_service)]):
    return await creators_service.get_creator_by_id(creator_id)


@router.post("", response_model=CreatorResponseTo, status_code=201)
async def create_creator(creator: CreatorRequestToAdd,
                         creators_service: Annotated[CreatorsService, Depends(get_creators_service)]):
    creator_response = await creators_service.create_creator(creator)
    return creator_response


@router.delete("/{creator_id}", status_code=204)
async def delete_creator(creator_id: int,
                         creators_service: Annotated[CreatorsService, Depends(get_creators_service)]):
    await creators_service.delete_creator(creator_id)
    return creator_id


@router.put("", response_model=CreatorResponseTo)
async def update_creator(creator: CreatorRequestToUpdate,
                         creators_service: Annotated[CreatorsService, Depends(get_creators_service)]):
    return await creators_service.update_creator(creator)

