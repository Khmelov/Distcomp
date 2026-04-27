from fastapi import APIRouter, Depends, HTTPException, status, Query, Response
from typing import List, Optional
from dto.requests import NoticeRequestTo
from dto.responses import NoticeResponseTo
from services.notice import NoticeService
from dependencies import get_notice_service

router = APIRouter(prefix="/notices", tags=["notices"])


@router.get("", response_model=List[NoticeResponseTo])
def get_all_notices(
    response: Response,
    page: int = Query(1, ge=1),
    size: int = Query(10, ge=1, le=100),
    sortBy: str = Query("id", pattern="^(id|topicId|content|created|modified)$"),
    order: str = Query("asc", pattern="^(asc|desc)$"),
    topicId: Optional[int] = Query(None),
    content: Optional[str] = Query(None),
    service: NoticeService = Depends(get_notice_service),
):
    filters = {}
    if topicId is not None:
        filters["topic_id"] = topicId
    if content:
        filters["content"] = content

    # Маппинг camelCase → snake_case для колонок в БД
    sort_mapping = {"topicId": "topic_id"}
    sort_column = sort_mapping.get(sortBy, sortBy)

    notices = service.get_list(
        filters=filters, page=page, size=size, sort_by=sort_column, order=order
    )
    total = service.count(filters)
    response.headers["X-Total-Count"] = str(total)
    return notices


@router.get("/{id}", response_model=NoticeResponseTo)
def get_notice(id: int, service: NoticeService = Depends(get_notice_service)):
    notice = service.get(id)
    if not notice:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Notice not found"
        )
    return notice


@router.post("", response_model=NoticeResponseTo, status_code=status.HTTP_201_CREATED)
def create_notice(
    request: NoticeRequestTo, service: NoticeService = Depends(get_notice_service)
):
    try:
        return service.create(request)
    except ValueError as e:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=str(e))


@router.put("/{id}", response_model=NoticeResponseTo)
def update_notice(
    id: int,
    request: NoticeRequestTo,
    service: NoticeService = Depends(get_notice_service),
):
    try:
        return service.update(id, request)
    except ValueError as e:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=str(e))


@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_notice(id: int, service: NoticeService = Depends(get_notice_service)):
    if not service.delete(id):
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Notice not found"
        )
