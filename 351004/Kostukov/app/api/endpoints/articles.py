from fastapi import APIRouter, status, Query
from typing import List, Optional
from app.core.articles.dto import (
    ArticleResponseTo, ArticleRequestTo
)
from app.core.articles.repo import InMemoryArticleRepo
from app.core.articles.service import ArticleService
from app.core.writers.dto import WriterResponseTo

try:
    from app.core.writers.repo import InMemoryWriterRepo as WriterRepoImpl
except Exception:
    WriterRepoImpl = None

try:
    from app.core.markers.repo import InMemoryMarkerRepo as MarkerRepoImpl
except Exception:
    MarkerRepoImpl = None

router = APIRouter(prefix="/api/v1.0/articles", tags=["articles"])

_article_repo = InMemoryArticleRepo()
_writer_repo = WriterRepoImpl() if WriterRepoImpl else None
_marker_repo = MarkerRepoImpl() if MarkerRepoImpl else None

article_service = ArticleService(_article_repo, _writer_repo, _marker_repo)

@router.post("", response_model=ArticleResponseTo, status_code=status.HTTP_201_CREATED)
@router.post("/", response_model=ArticleResponseTo, status_code=status.HTTP_201_CREATED)
async def create_article(dto: ArticleRequestTo):
    created = article_service.create_article(dto)
    return created

@router.get("", response_model=List[ArticleResponseTo])
@router.get("/", response_model=List[ArticleResponseTo])
async def list_articles(
    markerName: Optional[List[str]] = Query(default=None, alias="markerName"),
    markerId: Optional[List[int]] = Query(default=None, alias="markerId"),
    writerLogin: Optional[str] = Query(default=None, alias="writerLogin"),
    title: Optional[str] = Query(default=None),
    content: Optional[str] = Query(default=None),
):
    result = article_service.search_articles(
        marker_names=markerName,
        marker_ids=markerId,
        writer_login=writerLogin,
        title=title,
        content=content
    )
    return result

@router.get("/{article_id}", response_model=ArticleResponseTo)
async def get_article(article_id: int):
    resp = article_service.get_article_by_id(article_id)
    return resp

@router.put("/{article_id}", response_model=ArticleResponseTo)
async def update_article(article_id: int, payload: ArticleRequestTo):
    dto = payload
    resp = article_service.update_article(article_id, dto)
    return resp

@router.delete("/{article_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_article(article_id: int):
    article_service.delete_article(article_id)
    return None

@router.get("/{article_id}/markers")
async def get_article_markers(article_id: int):
    markers = article_service.get_markers_by_article_id(article_id)
    return [m.model_dump() for m in markers]

@router.get("/{article_id}/writer", response_model=WriterResponseTo)
async def get_article_writer(article_id: int):
    writer = article_service.get_writer_by_article_id(article_id)

    writer_dto = WriterResponseTo(
        id=writer.id,
        login=writer.login,
        firstname=writer.firstname,
        lastname=writer.lastname
    )

    return writer_dto
