from fastapi import APIRouter, HTTPException, status
from sqlalchemy import select

from dto import WriterResponseTo, WriterRequestTo
from models import Writer
from routers.db_router import db_dependency
from redis_cache import cache_get, cache_set, cache_delete

router = APIRouter(
    prefix="/api/v1.0/writers",
    tags=["tweet"],
)

CACHE_PREFIX = "writer"


@router.get("", response_model=list[WriterResponseTo])
async def get_writers(db: db_dependency):
    cached = await cache_get(f"{CACHE_PREFIX}:all")
    if cached is not None:
        return cached
    result = await db.execute(select(Writer))
    writers = result.scalars().all()
    data = [WriterResponseTo.model_validate(w, from_attributes=True).model_dump() for w in writers]
    await cache_set(f"{CACHE_PREFIX}:all", data)
    return data


@router.get("/{writer_id}", response_model=WriterResponseTo)
async def get_writer(writer_id: int, db: db_dependency):
    cached = await cache_get(f"{CACHE_PREFIX}:{writer_id}")
    if cached is not None:
        return cached
    result = await db.execute(select(Writer).where(Writer.id == writer_id))
    writer = result.scalars().first()
    if writer:
        data = WriterResponseTo.model_validate(writer, from_attributes=True).model_dump()
        await cache_set(f"{CACHE_PREFIX}:{writer_id}", data)
        return data
    return writer


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

    resp = WriterResponseTo.model_validate(writer, from_attributes=True).model_dump()
    await cache_set(f"{CACHE_PREFIX}:{writer.id}", resp)
    await cache_delete(f"{CACHE_PREFIX}:all")
    return resp


@router.put("/{writer_id}", status_code=200)
async def update_writer(writer_id: int, data: WriterRequestTo, db: db_dependency):
    writer = await db.execute(select(Writer).where(Writer.id == writer_id))
    writer = writer.scalars().first()
    for key, value in data.dict().items():
        setattr(writer, key, value)
    db.add(writer)
    await db.commit()

    resp = WriterResponseTo.model_validate(writer, from_attributes=True).model_dump()
    await cache_set(f"{CACHE_PREFIX}:{writer_id}", resp)
    await cache_delete(f"{CACHE_PREFIX}:all")
    return resp


@router.delete("/{writer_id}", status_code=204)
async def delete_writer(writer_id: int, db: db_dependency):
    writer = await db.get(Writer, writer_id)
    if not writer:
        raise HTTPException(status_code=404, detail="Writer not found")
    await db.delete(writer)
    await db.commit()

    await cache_delete(f"{CACHE_PREFIX}:{writer_id}")
    await cache_delete(f"{CACHE_PREFIX}:all")
