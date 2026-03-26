from fastapi import APIRouter, status
from typing import List
from app.core.markers.dto import (
    MarkerResponseTo,
    MarkerRequestTo
)
from app.core.markers.repo import InMemoryMarkerRepo
from app.core.markers.service import MarkerService

try:
    from app.core.articles.repo import InMemoryArticleRepo as ArticleRepoImpl
except Exception:
    ArticleRepoImpl = None

router = APIRouter(prefix="/api/v1.0/markers", tags=["markers"])

_marker_repo = InMemoryMarkerRepo()
_article_repo = ArticleRepoImpl() if ArticleRepoImpl else None
marker_service = MarkerService(_marker_repo, article_repo=_article_repo)

@router.post("", response_model=MarkerResponseTo, status_code=status.HTTP_201_CREATED)
@router.post("/", response_model=MarkerResponseTo, status_code=status.HTTP_201_CREATED)
async def create_marker(dto: MarkerRequestTo):
    created = marker_service.create_marker(dto)
    return created

@router.get("", response_model=List[MarkerResponseTo])
@router.get("/", response_model=List[MarkerResponseTo])
async def list_markers():
    return marker_service.list_markers()

@router.get("/{marker_id}", response_model=MarkerResponseTo)
async def get_marker(marker_id: int):
    resp = marker_service.get_marker_by_id(marker_id)
    return resp

@router.put("/{marker_id}", response_model=MarkerResponseTo)
async def update_marker(marker_id: int, payload: MarkerRequestTo):
    dto = payload
    resp = marker_service.update_marker(marker_id, dto)
    return resp

@router.delete("/{marker_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_marker(marker_id: int):
    marker_service.delete_marker(marker_id)
    return None
