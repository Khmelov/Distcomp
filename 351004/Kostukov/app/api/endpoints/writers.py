from fastapi import APIRouter, status
from app.core.writers.dto import WriterRequestTo, WriterResponseTo
from app.core.writers.service import WriterService
from app.core.writers.repo import InMemoryWriterRepo

router = APIRouter(prefix="/api/v1.0/writers", tags=["writers"])

_repo = InMemoryWriterRepo()
try:
    from app.core.articles.repo import InMemoryArticleRepo as InMemoryArticleRepoImpl
except Exception:
    InMemoryArticleRepoImpl = None

_article_repo = InMemoryArticleRepoImpl() if InMemoryArticleRepoImpl else None
_service = WriterService(_repo,_article_repo
                         )
@router.post("", response_model=WriterResponseTo, status_code=status.HTTP_201_CREATED)
@router.post("/", response_model=WriterResponseTo, status_code=status.HTTP_201_CREATED)
async def create_writer(dto: WriterRequestTo):
    created = _service.create_writer(dto)
    return created

@router.get("", response_model=list[WriterResponseTo])
@router.get("/", response_model=list[WriterResponseTo])
async def list_writers():
    return [w for w in _service.list_writers()]

@router.get("/{writer_id}", response_model=WriterResponseTo)
@router.get("/{writer_id}/", response_model=WriterResponseTo)
async def get_writer(writer_id: int):
    resp = _service.get_by_id(writer_id)
    return resp

@router.put("/{writer_id}", response_model=WriterResponseTo)
@router.put("/{writer_id}/", response_model=WriterResponseTo)
async def update_writer(writer_id: int, dto: WriterRequestTo):
    updated = _service.update_writer(writer_id, dto)
    return updated

@router.delete("/{writer_id}", status_code=status.HTTP_204_NO_CONTENT)
@router.delete("/{writer_id}/", status_code=status.HTTP_204_NO_CONTENT)
async def delete_writer(writer_id: int):
    _service.delete_writer(writer_id)
    return None

@router.get("/by-article/{article_id}", response_model=WriterResponseTo)
@router.get("/by-article/{article_id}/", response_model=WriterResponseTo)
async def get_writer_by_article(article_id: int):
    writer = _service.get_writer_by_article_id(article_id)
    return writer