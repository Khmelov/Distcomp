
from typing import List, Dict, Any, Optional, Tuple
from sqlalchemy import select, asc, desc
from sqlalchemy.ext.asyncio import AsyncSession
from app.infrastructure.db.models import Article as ArticleModel, article_marker, Marker as MarkerModel, Writer as WriterModel
from app.repositories.base import BaseRepository

class ArticleRepository(BaseRepository[ArticleModel]):
    def __init__(self, session: AsyncSession):
        self.session = session

    async def create(self, obj: ArticleModel) -> ArticleModel:
        self.session.add(obj)
        await self.session.flush()
        await self.session.commit()
        await self.session.refresh(obj)
        return obj

    async def get_by_id(self, id: int) -> Optional[ArticleModel]:
        q = select(ArticleModel).where(ArticleModel.id == id)
        res = await self.session.execute(q)
        return res.scalar_one_or_none()

    async def update(self, id: int, obj: ArticleModel) -> ArticleModel:
        existing = await self.get_by_id(id)
        if not existing:
            raise KeyError("not found")
        existing.title = obj.title
        existing.content = obj.content
        existing.writer_id = obj.writer_id
        existing.markers = obj.markers
        await self.session.commit()
        await self.session.refresh(existing)
        return existing

    async def delete(self, id: int) -> None:
        existing = await self.get_by_id(id)
        if not existing:
            raise KeyError("not found")
        await self.session.delete(existing)
        await self.session.commit()

    async def list(self, filters: Dict[str, Any] | None = None, page: int = 1, size: int = 20, sort: List[Tuple[str,str]] | None = None) -> List[ArticleModel]:
        q = select(ArticleModel).distinct()

        if filters:
            if "marker_ids" in filters and filters["marker_ids"]:
                q = q.join(article_marker, ArticleModel.id == article_marker.c.article_id).where(article_marker.c.marker_id.in_(filters["marker_ids"]))
            if "marker_names" in filters and filters["marker_names"]:
                q = q.join(article_marker, ArticleModel.id == article_marker.c.article_id).join(MarkerModel, article_marker.c.marker_id == MarkerModel.id).where(MarkerModel.name.in_(filters["marker_names"]))
            if "writer_login" in filters and filters["writer_login"]:
                q = q.join(WriterModel, ArticleModel.writer_id == WriterModel.id).where(WriterModel.login == filters["writer_login"])
            if "title" in filters and filters["title"]:
                q = q.where(ArticleModel.title.ilike(f"%{filters['title']}%"))
            if "content" in filters and filters["content"]:
                q = q.where(ArticleModel.content.ilike(f"%{filters['content']}%"))

        if sort:
            for field, direction in sort:
                col = getattr(ArticleModel, field, None)
                if col is not None:
                    q = q.order_by(desc(col) if direction.lower()=="desc" else asc(col))

        q = q.offset((page-1)*size).limit(size)
        res = await self.session.execute(q)
        return res.scalars().unique().all()