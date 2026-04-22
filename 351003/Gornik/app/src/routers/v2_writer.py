from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import select

from auth import current_user_dependency, hash_password, require_role
from dto import RegisterRequestTo, WriterResponseTo
from models import Writer
from routers.db_router import db_dependency
from redis_cache import cache_get, cache_set, cache_delete

router = APIRouter(
    prefix="/api/v2.0/writers",
    tags=["v2-writers"],
)

CACHE_PREFIX = "v2:writer"


@router.post("", response_model=WriterResponseTo, status_code=201)
async def register_writer(data: RegisterRequestTo, db: db_dependency):
    stmt = select(Writer).where(Writer.login == data.login)
    result = await db.execute(stmt)
    if result.scalars().first():
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail={"errorMessage": "Writer with this login already exists", "errorCode": 40302},
        )

    writer = Writer(
        login=data.login,
        password=hash_password(data.password),
        firstname=data.firstname,
        lastname=data.lastname,
        role=data.role.upper() if data.role else "CUSTOMER",
    )
    db.add(writer)
    await db.commit()
    await db.refresh(writer)

    resp = WriterResponseTo.model_validate(writer, from_attributes=True).model_dump()
    await cache_set(f"{CACHE_PREFIX}:{writer.id}", resp)
    await cache_delete(f"{CACHE_PREFIX}:all")
    return resp


@router.get("", response_model=list[WriterResponseTo])
async def get_writers(db: db_dependency, _user: current_user_dependency = None):
    cached = await cache_get(f"{CACHE_PREFIX}:all")
    if cached is not None:
        return cached
    result = await db.execute(select(Writer))
    writers = result.scalars().all()
    data = [WriterResponseTo.model_validate(w, from_attributes=True).model_dump() for w in writers]
    await cache_set(f"{CACHE_PREFIX}:all", data)
    return data


@router.get("/{writer_id}", response_model=WriterResponseTo)
async def get_writer(writer_id: int, db: db_dependency, _user: current_user_dependency = None):
    cached = await cache_get(f"{CACHE_PREFIX}:{writer_id}")
    if cached is not None:
        return cached
    result = await db.execute(select(Writer).where(Writer.id == writer_id))
    writer = result.scalars().first()
    if not writer:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Writer not found", "errorCode": 40401},
        )
    data = WriterResponseTo.model_validate(writer, from_attributes=True).model_dump()
    await cache_set(f"{CACHE_PREFIX}:{writer_id}", data)
    return data


@router.put("/{writer_id}", response_model=WriterResponseTo)
async def update_writer(
    writer_id: int,
    data: RegisterRequestTo,
    db: db_dependency,
    _admin: Writer = Depends(require_role("ADMIN")),
):
    result = await db.execute(select(Writer).where(Writer.id == writer_id))
    writer = result.scalars().first()
    if not writer:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Writer not found", "errorCode": 40402},
        )
    writer.login = data.login
    writer.password = hash_password(data.password)
    writer.firstname = data.firstname
    writer.lastname = data.lastname
    writer.role = data.role.upper() if data.role else writer.role
    db.add(writer)
    await db.commit()
    await db.refresh(writer)

    resp = WriterResponseTo.model_validate(writer, from_attributes=True).model_dump()
    await cache_set(f"{CACHE_PREFIX}:{writer_id}", resp)
    await cache_delete(f"{CACHE_PREFIX}:all")
    return resp


@router.delete("/{writer_id}", status_code=204)
async def delete_writer(
    writer_id: int,
    db: db_dependency,
    _admin: Writer = Depends(require_role("ADMIN")),
):
    writer = await db.get(Writer, writer_id)
    if not writer:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Writer not found", "errorCode": 40403},
        )
    await db.delete(writer)
    await db.commit()

    await cache_delete(f"{CACHE_PREFIX}:{writer_id}")
    await cache_delete(f"{CACHE_PREFIX}:all")
