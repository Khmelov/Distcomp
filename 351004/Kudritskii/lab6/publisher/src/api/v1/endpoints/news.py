from http import HTTPStatus
from typing import List

from fastapi import APIRouter, Depends

from src.deps import get_news_service
from src.schemas.news import NewsResponseTo, NewsRequestTo
from src.services import NewsService

router = APIRouter(prefix="/newss")

@router.get("", response_model=List[NewsResponseTo], status_code=HTTPStatus.OK)
async def get_all(service: NewsService = Depends(get_news_service)):
    return await service.get_all()

@router.get("/{news_id}", response_model=NewsResponseTo, status_code=HTTPStatus.OK)
async def get_by_id(news_id: int, service: NewsService = Depends(get_news_service)):
    return await service.get_one(news_id)

@router.post("", response_model=NewsResponseTo, status_code=HTTPStatus.CREATED)
async def create(dto: NewsRequestTo, service: NewsService = Depends(get_news_service)):
    return await service.create(dto)

@router.put("/{news_id}", response_model=NewsResponseTo, status_code=HTTPStatus.OK)
async def put(news_id: int, dto: NewsRequestTo, service: NewsService = Depends(get_news_service)):
    return await service.update(news_id, dto)

@router.delete("/{news_id}", status_code=HTTPStatus.NO_CONTENT)
async def delete(news_id: int, service: NewsService = Depends(get_news_service)):
    return await service.delete(news_id)