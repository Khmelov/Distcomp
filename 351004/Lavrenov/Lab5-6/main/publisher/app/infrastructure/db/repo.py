from typing import Any, Dict, List, Optional
from sqlalchemy import select, delete, desc
from sqlalchemy.ext.asyncio import AsyncSession
from publisher.app.infrastructure.db import models


class AsyncBaseRepository:
    def __init__(self, model):
        self.model = model

    async def get_all(
            self,
            db: AsyncSession,
            skip: int = 0,
            limit: int = 10,
            sort_by: str = "id",
            **filters: Any
    ) -> List[Any]:
        stmt = select(self.model)

        where_clauses = []
        for attr, value in filters.items():
            if value is None:
                continue
            if hasattr(self.model, attr):
                column = getattr(self.model, attr)
                if isinstance(value, str):
                    where_clauses.append(column.ilike(f"%{value}%"))
                else:
                    where_clauses.append(column == value)

        if where_clauses:
            stmt = stmt.where(*where_clauses)

        order_col = getattr(self.model, sort_by) if hasattr(self.model, sort_by) else getattr(self.model, "id")
        stmt = stmt.order_by(desc(order_col)).offset(skip).limit(limit)

        result = await db.scalars(stmt)
        return result.all()

    async def get_by_id(self, db: AsyncSession, obj_id: int) -> Optional[Any]:
        stmt = select(self.model).where(self.model.id == obj_id)
        result = await db.scalars(stmt)
        return result.first()

    async def create(self, db: AsyncSession, data: Dict[str, Any]) -> Any:
        obj = self.model(**data)
        db.add(obj)
        await db.commit()
        await db.refresh(obj)
        return obj

    async def update(self, db: AsyncSession, obj_id: int, data: Dict[str, Any]) -> Optional[Any]:
        obj = await self.get_by_id(db, obj_id)
        if not obj:
            return None
        for key, val in data.items():
            if hasattr(obj, key):
                setattr(obj, key, val)
        await db.commit()
        await db.refresh(obj)
        return obj

    async def delete(self, db: AsyncSession, obj_id: int) -> bool:
        stmt = delete(self.model).where(self.model.id == obj_id)
        result = await db.execute(stmt)
        await db.commit()
        return (result.rowcount or 0) > 0

class WriterRepo(AsyncBaseRepository):
    def __init__(self): super().__init__(models.Writer)

class ArticleRepo(AsyncBaseRepository):
    def __init__(self): super().__init__(models.Article)

class NoteRepo(AsyncBaseRepository):
    def __init__(self): super().__init__(models.Note)

class MarkerRepo(AsyncBaseRepository):
    def __init__(self): super().__init__(models.Marker)