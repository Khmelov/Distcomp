from __future__ import annotations

from typing import Any, Generic, Optional, Type, TypeVar

from sqlalchemy import Select, asc, desc, func, select
from sqlalchemy.ext.asyncio import AsyncSession

from publisher.repositories.base import AbstractRepository
from publisher.schemas.common import Page, PaginationParams


ModelT = TypeVar("ModelT")


class SQLAlchemyRepository(AbstractRepository[ModelT], Generic[ModelT]):
    def __init__(self, model: Type[ModelT], session: AsyncSession) -> None:
        self._model = model
        self._session = session

    async def create(self, obj_in: ModelT) -> ModelT:
        self._session.add(obj_in)  # type: ignore[arg-type]
        await self._session.flush()
        await self._session.refresh(obj_in)  # type: ignore[arg-type]
        return obj_in

    async def get_by_id(self, obj_id: int) -> Optional[ModelT]:
        stmt = select(self._model).where(self._model.id == obj_id)  # type: ignore[attr-defined]
        res = await self._session.execute(stmt)
        return res.scalar_one_or_none()

    async def _apply_filters(self, stmt: Select[Any], filters: Optional[dict[str, Any]]) -> Select[Any]:
        if not filters:
            return stmt
        for field, value in filters.items():
            if value is None:
                continue
            column = getattr(self._model, field, None)
            if column is not None:
                stmt = stmt.where(column == value)
        return stmt

    async def get_all(
        self,
        pagination: PaginationParams,
        filters: Optional[dict[str, Any]] = None,
    ) -> Page[ModelT]:
        stmt = select(self._model)
        stmt = await self._apply_filters(stmt, filters)

        if pagination.sort_by:
            column = getattr(self._model, pagination.sort_by, None)
            if column is not None:
                order_func = asc if pagination.sort_order == "asc" else desc
                stmt = stmt.order_by(order_func(column))

        total_stmt = select(func.count()).select_from(stmt.subquery())
        total_res = await self._session.execute(total_stmt)
        total = int(total_res.scalar_one() or 0)

        offset = (pagination.page - 1) * pagination.size
        stmt = stmt.offset(offset).limit(pagination.size)

        res = await self._session.execute(stmt)
        items = list(res.scalars().all())
        return Page[ModelT](items=items, total=total, page=pagination.page, size=pagination.size)

    async def update(self, obj_id: int, obj_in: ModelT) -> Optional[ModelT]:
        existing = await self.get_by_id(obj_id)
        if existing is None:
            return None
        for attr, value in vars(obj_in).items():
            if attr == "id":
                continue
            setattr(existing, attr, value)
        await self._session.flush()
        await self._session.refresh(existing)  # type: ignore[arg-type]
        return existing

    async def delete(self, obj_id: int) -> bool:
        existing = await self.get_by_id(obj_id)
        if existing is None:
            return False
        await self._session.delete(existing)  # type: ignore[arg-type]
        await self._session.flush()
        return True

