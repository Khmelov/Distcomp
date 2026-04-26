from fastapi import APIRouter, Depends, Query
from typing import List, Optional
from http import HTTPStatus

from Task320.src.api.v1.dep import get_creator_service
from Task320.src.schemas.creator import CreatorResponseTo, CreatorRequestTo
from Task320.src.services.creator import CreatorService

router = APIRouter(prefix="/creators")

@router.get("", response_model=List[CreatorResponseTo], status_code=HTTPStatus.OK)
async def get_all(
    page: int = Query(1, ge=1),
    size: int = Query(20, ge=1, le=100),
    sort: str = Query("id", description="Field to sort by (e.g. 'id', 'login')"),
    login: Optional[str] = Query(None, description="Filter by login (partial match)"),
    firstname: Optional[str] = Query(None, description="Filter by firstname (partial match)"),
    lastname: Optional[str] = Query(None, description="Filter by lastname (partial match)"),
    service: CreatorService = Depends(get_creator_service)
):
    return await service.get_all(
        page=page, size=size, sort=sort,
        login=login, firstname=firstname, lastname=lastname
    )

@router.get("/{creator_id}", response_model=CreatorResponseTo, status_code=HTTPStatus.OK)
async def get_by_id(creator_id: str, service: CreatorService = Depends(get_creator_service)):
    return await service.get_one(creator_id)

@router.get("/by_tweet/{tweet_id}", response_model=CreatorResponseTo, status_code=HTTPStatus.OK)
async def get_by_tweet_id(tweet_id: int, service: CreatorService = Depends(get_creator_service)):
    return await service.get_creator_by_tweet_id(tweet_id)

@router.post("", response_model=CreatorResponseTo, status_code=HTTPStatus.CREATED)
async def create(dto: CreatorRequestTo, service: CreatorService = Depends(get_creator_service)):
    return await service.create(dto)

@router.put("/{creator_id}", response_model=CreatorResponseTo, status_code=HTTPStatus.OK)
async def update(creator_id: str, dto: CreatorRequestTo, service: CreatorService = Depends(get_creator_service)):
    return await service.update(creator_id, dto)

@router.delete("/{creator_id}", status_code=HTTPStatus.NO_CONTENT)
async def delete(creator_id: str, service: CreatorService = Depends(get_creator_service)):
    await service.delete(creator_id)