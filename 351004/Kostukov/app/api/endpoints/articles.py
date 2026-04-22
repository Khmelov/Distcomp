from fastapi import APIRouter, status, Query, Depends, Body, HTTPException
from typing import List, Optional
from app.core.articles.dto import (
    ArticleResponseTo, ArticleRequestTo
)
from app.core.articles.repo import InMemoryArticleRepo
from app.core.articles.service import ArticleService as InMemoryArticleService
from app.core.writers.dto import WriterResponseTo
from app.services.article_service import ArticleService
from app.infrastructure.db.session import get_session
from sqlalchemy.ext.asyncio import AsyncSession

try:
    from app.core.writers.repo import InMemoryWriterRepo as WriterRepoImpl
except Exception:
    WriterRepoImpl = None

try:
    from app.core.markers.repo import InMemoryMarkerRepo as MarkerRepoImpl
except Exception:
    MarkerRepoImpl = None

router = APIRouter(prefix="/api/v1.0/articles", tags=["articles"])
service = ArticleService()

_article_repo = InMemoryArticleRepo()
_writer_repo = WriterRepoImpl() if WriterRepoImpl else None
_marker_repo = MarkerRepoImpl() if MarkerRepoImpl else None

article_service = InMemoryArticleService(_article_repo, _writer_repo, _marker_repo)

@router.post("", response_model=ArticleResponseTo, status_code=status.HTTP_201_CREATED)
@router.post("/", response_model=ArticleResponseTo, status_code=status.HTTP_201_CREATED)
async def create_article(dto: ArticleRequestTo = Body(...), session: AsyncSession = Depends(get_session)):
    res = await service.create(session, dto)
    return ArticleResponseTo(
        id=res.id,
        writerId= res.writer_id, title = res.title,
        content = res.content, created = res.created, modified = res.modified
    )

@router.get("", response_model=List[ArticleResponseTo])
@router.get("/", response_model=List[ArticleResponseTo])
async def list_articles(skip: int = 0, limit: int = 100, session: AsyncSession = Depends(get_session)):
    items = await service.get_all(session,skip, limit)
    return [
        ArticleResponseTo(
            id = i.id, writerId= i.writer_id,
            title = i.title, content = i.content,
            created = i.created, modified = i.modified
        ) for i in items
    ]

@router.get("/{article_id}", response_model=ArticleResponseTo)
async def get_article(article_id: int, session: AsyncSession = Depends(get_session)):
    item = await service.get_by_id(session, article_id)
    if not item: raise HTTPException(status_code=404, detail="Article not found")
    return ArticleResponseTo(
        id=item.id, writerId=item.writer_id,
        title=item.title, content=item.content,
        created=item.created, modified=item.modified
    )

@router.put("/{article_id}", response_model=ArticleResponseTo)
async def update_article(article_id: int, payload: ArticleRequestTo = Body(...), session: AsyncSession = Depends(get_session)):
    item = await service.update(session, article_id, payload)
    if not item : raise HTTPException(status_code=404, detail="Article not found")
    return ArticleResponseTo(
        id=item.id, writerId=item.writer_id,
        title=item.title, content=item.content,
        created=item.created, modified=item.modified
    )

@router.delete("/{article_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_article(article_id: int, session: AsyncSession = Depends(get_session)):
    if not await service.delete(session, article_id):
        raise HTTPException(status_code=404, detail="Article not found")

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
