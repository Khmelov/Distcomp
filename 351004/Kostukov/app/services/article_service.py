from fastapi import HTTPException
from sqlalchemy import select, delete, func
from sqlalchemy.exc import IntegrityError
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.articles.dto import ArticleRequestTo
from app.infrastructure.db import models
from app.infrastructure.db.repo import ArticleRepo, WriterRepo


class ArticleService:
    def __init__(self):
        self.repo = ArticleRepo()
        self.writer_repo = WriterRepo()

    async def _cleanup_unused_markers(self, session: AsyncSession):
        stmt = (
            select(models.Marker)
            .outerjoin(
                models.article_marker,
                models.Marker.id == models.article_marker.c.marker_id
            )
            .group_by(models.Marker.id)
            .having(func.count(models.article_marker.c.article_id) == 0)
        )

        unused_markers = (await session.scalars(stmt)).all()

        for marker in unused_markers:
            await session.delete(marker)

    async def _get_or_create_marker(self, session: AsyncSession, name: str) -> models.Marker:
        normalized = name.strip()
        marker = await session.scalar(
            select(models.Marker).where(models.Marker.name == normalized)
        )
        if marker is None:
            marker = models.Marker(name=normalized)
            session.add(marker)
            await session.flush()
        return marker

    async def _sync_article_markers(
        self,
        session: AsyncSession,
        article_id: int,
        marker_names: list[str] | None,
    ) -> None:
        if marker_names is None:
            return

        await session.execute(
            delete(models.article_marker).where(
                models.article_marker.c.article_id == article_id
            )
        )

        unique_names: list[str] = []
        seen: set[str] = set()
        for name in marker_names:
            if not isinstance(name, str):
                continue
            normalized = name.strip()
            if normalized and normalized not in seen:
                seen.add(normalized)
                unique_names.append(normalized)

        for marker_name in unique_names:
            marker = await self._get_or_create_marker(session, marker_name)

            await session.execute(
                models.article_marker.insert().values(
                    article_id=article_id,
                    marker_id=marker.id,
                )
            )

    async def create(self, session: AsyncSession, dto: ArticleRequestTo):
        writer = await self.writer_repo.get_by_id(session, dto.writer_id)
        if not writer:
            raise HTTPException(status_code=403, detail="Writer not found")

        existing = await session.scalar(
            select(models.Article).where(models.Article.title == dto.title)
        )
        if existing:
            raise HTTPException(status_code=403, detail="Article already exists")

        article = models.Article(
            writer_id=dto.writer_id,
            title=dto.title,
            content=dto.content,
        )
        session.add(article)

        try:
            await session.flush()

            await self._sync_article_markers(session, article.id, dto.markers)

            await session.commit()
            await session.refresh(article)
            return article

        except IntegrityError:
            await session.rollback()
            raise HTTPException(status_code=403, detail="Invalid article data")

    async def get_all(self, session: AsyncSession, skip=0, limit=10):
        return await self.repo.get_all(session, skip=skip, limit=limit)

    async def get_by_id(self, session: AsyncSession, id: int):
        return await self.repo.get_by_id(session, id)

    async def update(self, session: AsyncSession, id: int, dto: ArticleRequestTo):
        article = await self.repo.get_by_id(session, id)
        if not article:
            return None

        writer = await self.writer_repo.get_by_id(session, dto.writer_id)
        if not writer:
            raise HTTPException(status_code=403, detail="Writer not found")

        other = await session.scalar(
            select(models.Article).where(
                models.Article.title == dto.title,
                models.Article.id != id,
            )
        )
        if other:
            raise HTTPException(status_code=403, detail="Article already exists")

        article.writer_id = dto.writer_id
        article.title = dto.title
        article.content = dto.content

        try:
            await self._sync_article_markers(session, article.id, dto.markers)

            await session.commit()
            await session.refresh(article)
            return article

        except IntegrityError:
            await session.rollback()
            raise HTTPException(status_code=403, detail="Invalid article data")

    async def delete(self, session: AsyncSession, id: int):
        article = await self.repo.get_by_id(session, id)
        if not article:
            return False

        try:
            await session.delete(article)
            await session.flush()

            await self._cleanup_unused_markers(session)

            await session.commit()
            return True

        except Exception:
            await session.rollback()
            raise HTTPException(status_code=403, detail="Invalid delete operation")
