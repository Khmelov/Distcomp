from fastapi import APIRouter, status, Depends, Body, HTTPException
from typing import List
from sqlalchemy.ext.asyncio import AsyncSession
from app.infrastructure.db.session import get_session
from app.services.marker_service import MarkerService
from app.core.markers.dto import (
    MarkerResponseTo,
    MarkerRequestTo
)
from app.core.markers.repo import InMemoryMarkerRepo
from app.core.markers.service import MarkerService as InMemoryMarkerService

try:
    from app.core.articles.repo import InMemoryArticleRepo as ArticleRepoImpl
except Exception:
    ArticleRepoImpl = None

router = APIRouter(prefix="/api/v1.0/markers", tags=["markers"])

_marker_repo = InMemoryMarkerRepo()
_article_repo = ArticleRepoImpl() if ArticleRepoImpl else None
marker_service = InMemoryMarkerService(_marker_repo, article_repo=_article_repo)
service = MarkerService()

@router.post("", response_model=MarkerResponseTo, status_code=status.HTTP_201_CREATED)
@router.post("/", response_model=MarkerResponseTo, status_code=status.HTTP_201_CREATED)
async def create_marker(dto: MarkerRequestTo, session: AsyncSession = Depends(get_session)):
    return await service.create(session, dto)

@router.get("", response_model=List[MarkerResponseTo])
@router.get("/", response_model=List[MarkerResponseTo])
async def list_markers(session: AsyncSession = Depends(get_session)):
    return await service.get_all(session)

@router.get("/{marker_id}", response_model=MarkerResponseTo)
async def get_marker(marker_id: int, session: AsyncSession = Depends(get_session)):
    item = await service.get_by_id(session, marker_id)
    if not item:
        raise HTTPException(status_code=404, detail="Marker not found")
    return item

@router.put("/{marker_id}", response_model=MarkerResponseTo)
async def update_marker(marker_id: int, payload: MarkerRequestTo = Body(...), session: AsyncSession = Depends(get_session)):
    item = await service.update(session, marker_id, payload)
    if not item:
        raise HTTPException(status_code=404, detail="Marker not found")
    return item


@router.delete("/{marker_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_marker(marker_id: int, session: AsyncSession = Depends(get_session)):
    if not await service.delete(session,marker_id):
        raise HTTPException(status_code=404, detail="Marker not found")
