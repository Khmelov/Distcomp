from typing import List

from fastapi import APIRouter, Depends, HTTPException, status

from app.dtos.author_response import AuthorResponseTo
from app.dtos.mark_response import MarkResponseTo
from app.dtos.message_response import MessageResponseTo
from app.dtos.news_request import NewsRequestTo
from app.dtos.news_response import NewsResponseTo
from app.services.message_service import MessageService
from app.services.news_service import NewsService

router = APIRouter(prefix="/api/v1.0", tags=["news"])


def get_news_service() -> NewsService:
    from main import news_service
    return news_service


def get_message_service() -> MessageService:
    from main import message_service
    return message_service


@router.post("/news", response_model=NewsResponseTo, status_code=status.HTTP_201_CREATED)
def create_news(
    dto: NewsRequestTo,
    service: NewsService = Depends(get_news_service),
) -> NewsResponseTo:
    try:
        return service.create_news(dto)
    except ValueError as ex:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={"errorMessage": str(ex), "errorCode": 40001},
        ) from ex


@router.get("/news", response_model=List[NewsResponseTo])
def list_news(
    service: NewsService = Depends(get_news_service),
) -> List[NewsResponseTo]:
    return service.get_all_news()


@router.get("/news/{news_id}", response_model=NewsResponseTo)
def get_news(
    news_id: int,
    service: NewsService = Depends(get_news_service),
) -> NewsResponseTo:
    news = service.get_news(news_id)
    if not news:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "News not found", "errorCode": 40401},
        )
    return news


@router.put("/news/{news_id}", response_model=NewsResponseTo)
def update_news(
    news_id: int,
    dto: NewsRequestTo,
    service: NewsService = Depends(get_news_service),
) -> NewsResponseTo:
    try:
        updated = service.update_news(news_id, dto)
    except ValueError as ex:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={"errorMessage": str(ex), "errorCode": 40001},
        ) from ex

    if not updated:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "News not found", "errorCode": 40401},
        )
    return updated


@router.delete("/news/{news_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_news(
    news_id: int,
    service: NewsService = Depends(get_news_service),
) -> None:
    deleted = service.delete_news(news_id)
    if not deleted:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "News not found", "errorCode": 40401},
        )


@router.get("/news/{news_id}/author", response_model=AuthorResponseTo)
def get_author_by_news(
    news_id: int,
    service: NewsService = Depends(get_news_service),
) -> AuthorResponseTo:
    author = service.get_author_by_news(news_id)
    if not author:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Author or news not found", "errorCode": 40401},
        )
    return author


@router.get("/news/{news_id}/marks", response_model=List[MarkResponseTo])
def get_marks_by_news(
    news_id: int,
    service: NewsService = Depends(get_news_service),
) -> List[MarkResponseTo]:
    marks = service.get_marks_by_news(news_id)
    if marks is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "News not found", "errorCode": 40401},
        )
    return marks


@router.get("/news/{news_id}/messages", response_model=List[MessageResponseTo])
def get_messages_by_news(
    news_id: int,
    service: MessageService = Depends(get_message_service),
) -> List[MessageResponseTo]:
    try:
        return service.get_messages_by_news(news_id)
    except ValueError as ex:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": str(ex), "errorCode": 40401},
        ) from ex