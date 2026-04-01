
from fastapi import APIRouter, HTTPException
from sqlalchemy import select

from dto import WriterResponseTo, WriterRequestTo
from models import Writer
from routers.db_router import db_dependency

router = APIRouter(
    prefix="/api/v1.0/writers",
    tags=["tweet"],
)

@router.get("", response_model=list[WriterResponseTo])
async def get_writers(db: db_dependency):
    result = await db.execute(select(Writer))
    writers = result.scalars().all()
    return writers

@router.get("/{writer_id}", response_model=WriterResponseTo)
async def get_writers(writer_id: int, db: db_dependency):
    result = await db.execute(select(Writer).where(Writer.id == writer_id))
    writer = result.scalars().first()
    return writer

# @router.post("", status_code=201)
# async def create_writer(request: Request):
#     # Заголовки
#     logger.debug("HEADERS: %s", dict(request.headers))
#
#     # Query параметры
#     logger.debug("QUERY PARAMS: %s", dict(request.query_params))
#
#     # Cookies
#     logger.debug("COOKIES: %s", request.cookies)
#
#     # IP клиента
#     logger.debug("CLIENT: %s:%s", request.client.host, request.client.port)
#
#     # Сырой body
#     raw = await request.body()
#     logger.debug("RAW BODY: %s", raw)
#
#     # Попытка парсинга JSON
#     try:
#         body_json = await request.json()
#     except Exception:
#         body_json = None
#     logger.debug("PARSED JSON: %s", body_json)
#
#     return {"status": "ok"}

from fastapi import APIRouter, HTTPException, status
from sqlalchemy import select


# ... ваши импорты ...

@router.post("", status_code=201)
async def create_writer(data: WriterRequestTo, db: db_dependency):
    stmt = select(Writer).where(Writer.login == data.login)
    result = await db.execute(stmt)
    ex_writer = result.scalars().first()

    if ex_writer:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Writer with this login already exists"
        )

    writer_data = data.model_dump() if hasattr(data, "model_dump") else data.dict()
    writer = Writer(**writer_data)

    db.add(writer)
    await db.commit()
    return writer

@router.put("/{writer_id}", status_code=200)
async def update_writer(writer_id:int, data: WriterRequestTo, db: db_dependency):
    writer = await db.execute(select(Writer).where(Writer.id == writer_id))
    writer = writer.scalars().first()
    for key, value in data.dict().items():
        setattr(writer, key, value)
    db.add(writer)
    await db.commit()
    return writer

@router.delete("/{writer_id}", status_code=204)
async def delete_writer(writer_id: int, db: db_dependency):
    writer = await db.get(Writer, writer_id)
    if not writer:
        raise HTTPException(status_code=404, detail="Writer not found")
    await db.delete(writer)
    await db.commit()