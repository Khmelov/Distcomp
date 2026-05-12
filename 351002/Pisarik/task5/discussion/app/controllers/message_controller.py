from typing import List, Optional

from fastapi import APIRouter, Depends, HTTPException, Query, status

from app.dtos.message_request import MessageRequestTo
from app.dtos.message_response import MessageResponseTo
from app.services.message_service import MessageService, PageParams

router = APIRouter(prefix="/api/v1.0/messages", tags=["messages"])


def get_message_service() -> MessageService:
    return MessageService()


@router.post(
    "",
    response_model=MessageResponseTo,
    response_model_exclude_none=True,
    status_code=status.HTTP_201_CREATED,
)
def create_message(
    dto: MessageRequestTo,
    service: MessageService = Depends(get_message_service),
) -> MessageResponseTo:
    try:
        return service.create_message(dto)
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"errorMessage": f"{type(e).__name__}: {e}", "errorCode": 50002},
        ) from e


@router.get("", response_model=List[MessageResponseTo], response_model_exclude_none=True)
def list_messages(
    page: int = Query(0, ge=0),
    size: int = Query(20, ge=1, le=200),
    sort: str = Query("id,asc"),
    newsId: Optional[int] = None,
    content: Optional[str] = None,
    country: Optional[str] = None,
    service: MessageService = Depends(get_message_service),
) -> List[MessageResponseTo]:
    try:
        return service.get_all_messages(
            PageParams(page=page, size=size, sort=sort),
            news_id=newsId,
            content=content,
            country_override=country,
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"errorMessage": f"{type(e).__name__}: {e}", "errorCode": 50002},
        ) from e


@router.get("/{message_id}", response_model=MessageResponseTo, response_model_exclude_none=True)
def get_message(
    message_id: int,
    service: MessageService = Depends(get_message_service),
) -> MessageResponseTo:
    try:
        message = service.get_message(message_id)
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"errorMessage": f"{type(e).__name__}: {e}", "errorCode": 50002},
        ) from e
    if not message:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Message not found", "errorCode": 40401},
        )
    return message


@router.put("/{message_id}", response_model=MessageResponseTo, response_model_exclude_none=True)
def update_message(
    message_id: int,
    dto: MessageRequestTo,
    service: MessageService = Depends(get_message_service),
) -> MessageResponseTo:
    try:
        updated = service.update_message(message_id, dto)
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"errorMessage": f"{type(e).__name__}: {e}", "errorCode": 50002},
        ) from e
    if not updated:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Message not found", "errorCode": 40401},
        )
    return updated


@router.delete("/{message_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_message(
    message_id: int,
    service: MessageService = Depends(get_message_service),
) -> None:
    try:
        ok = service.delete_message(message_id)
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail={"errorMessage": f"{type(e).__name__}: {e}", "errorCode": 50002},
        ) from e
    if not ok:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Message not found", "errorCode": 40401},
        )
