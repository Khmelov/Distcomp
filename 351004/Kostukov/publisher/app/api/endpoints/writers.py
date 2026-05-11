from fastapi import APIRouter, status, Depends, HTTPException, Body
from publisher.app.core.writers.dto import WriterRequestTo, WriterResponseTo
from publisher.app.core.writers.service import WriterService as InMemoryWriterService
from publisher.app.core.writers.repo import InMemoryWriterRepo
from publisher.app.services.writer_service import WriterService
from publisher.app.infrastructure.db.session import get_session
from sqlalchemy.ext.asyncio import AsyncSession

router = APIRouter(prefix="/api/v1.0/writers", tags=["writers"])

_repo = InMemoryWriterRepo()
try:
    from publisher.app.core.articles.repo import InMemoryArticleRepo as InMemoryArticleRepoImpl
except Exception:
    InMemoryArticleRepoImpl = None

_article_repo = InMemoryArticleRepoImpl() if InMemoryArticleRepoImpl else None
_service = InMemoryWriterService(_repo,_article_repo)
service = WriterService()
@router.post("", response_model=WriterResponseTo, status_code=status.HTTP_201_CREATED)
@router.post("/", response_model=WriterResponseTo, status_code=status.HTTP_201_CREATED)
async def create_writer(dto: WriterRequestTo, session: AsyncSession = Depends(get_session)):
    return await service.create(session, dto)

@router.get("", response_model=list[WriterResponseTo])
@router.get("/", response_model=list[WriterResponseTo])
async def list_writers(skip: int = 0, limit : int = 10,session: AsyncSession = Depends(get_session)):
    return await service.get_all(session, skip, limit)

@router.get("/{writer_id}", response_model=WriterResponseTo)
@router.get("/{writer_id}/", response_model=WriterResponseTo)
async def get_writer(writer_id: int, session: AsyncSession = Depends(get_session)):
    item = await service.get_by_id(session, writer_id)
    if not item:
        raise HTTPException(status_code=404, detail="Writer not found")
    return item

@router.put("/{writer_id}", response_model=WriterResponseTo)
@router.put("/{writer_id}/", response_model=WriterResponseTo)
async def update_writer(writer_id: int, payload: WriterRequestTo = Body(...), session: AsyncSession = Depends(get_session)):
    item = await service.update(session ,writer_id, payload)
    if not item:
        raise HTTPException(status_code=404, detail="Writer not found")
    return item

@router.delete("/{writer_id}", status_code=status.HTTP_204_NO_CONTENT)
@router.delete("/{writer_id}/", status_code=status.HTTP_204_NO_CONTENT)
async def delete_writer(writer_id: int, session: AsyncSession = Depends(get_session)):
    if not await service.delete(session, writer_id):
        raise HTTPException(status_code=404, detail="Writer not found")

@router.get("/by-article/{article_id}", response_model=WriterResponseTo)
@router.get("/by-article/{article_id}/", response_model=WriterResponseTo)
async def get_writer_by_article(article_id: int):
    writer = _service.get_writer_by_article_id(article_id)
    return writer