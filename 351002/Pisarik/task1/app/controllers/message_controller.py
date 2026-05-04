from typing import List

from fastapi import APIRouter, Depends, HTTPException, status

from app.dtos.message_request import MessageRequestTo
from app.dtos.message_response import MessageResponseTo
from app.services.message_service import MessageService

router = APIRouter(prefix="/api/v1.0/messages", tags=["messages"])


def get_message_service() -> MessageService:
    from main import message_service
    return message_service


@router.post("", response_model=MessageResponseTo, status_code=status.HTTP_201_CREATED)
def create_message(
    dto: MessageRequestTo,
    service: MessageService = Depends(get_message_service),
) -> MessageResponseTo:
    try:
        return service.create_message(dto)
    except ValueError as ex:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={"errorMessage": str(ex), "errorCode": 40001},
        ) from ex


@router.get("", response_model=List[MessageResponseTo])
def list_messages(
    service: MessageService = Depends(get_message_service),
) -> List[MessageResponseTo]:
    return service.get_all_messages()


@router.get("/{message_id}", response_model=MessageResponseTo)
def get_message(
    message_id: int,
    service: MessageService = Depends(get_message_service),
) -> MessageResponseTo:
    message = service.get_message(message_id)
    if not message:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Message not found", "errorCode": 40401},
        )
    return message


@router.put("/{message_id}", response_model=MessageResponseTo)
def update_message(
    message_id: int,
    dto: MessageRequestTo,
    service: MessageService = Depends(get_message_service),
) -> MessageResponseTo:
    try:
        updated = service.update_message(message_id, dto)
    except ValueError as ex:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={"errorMessage": str(ex), "errorCode": 40001},
        ) from ex

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
    deleted = service.delete_message(message_id)
    if not deleted:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Message not found", "errorCode": 40401},
        )