from __future__ import annotations
from sqlalchemy.ext.asyncio import AsyncSession
from typing import Any, Dict, List, Optional

import httpx
from fastapi import HTTPException
from publisher.app.infrastructure.db.repo import ArticleRepo


class NoteServiceCassandra:
    def __init__(self, base_url: str = "http://localhost:24130"):
        self.base_url = base_url.rstrip("/")
        self.article_repo = ArticleRepo()

    async def create(self, dto: Any, session: AsyncSession) -> Dict[str, Any]:
        payload = dto.model_dump(exclude_none=True, by_alias=True)

        article_id = payload.get("articleId")

        article = await self.article_repo.get_by_id(session, article_id)
        if not article:
            raise HTTPException(status_code=404, detail="Article not found")

        async with httpx.AsyncClient(timeout=10.0) as client:
            response = await client.post(
                f"{self.base_url}/api/v1.0/notes",
                json=payload
            )

        if response.status_code not in (200, 201):
            raise HTTPException(
                status_code=response.status_code,
                detail=response.text
            )

        return response.json()

    async def get_all(self, skip: int = 0, limit: int = 10) -> List[Dict[str, Any]]:
        params = {"skip": skip, "limit": limit}

        async with httpx.AsyncClient(timeout=10.0) as client:
            response = await client.get(f"{self.base_url}/api/v1.0/notes", params=params)

        if response.status_code != 200:
            raise HTTPException(status_code=response.status_code, detail=response.text)

        return response.json()

    async def get_by_id(self, note_id: int) -> Optional[Dict[str, Any]]:
        async with httpx.AsyncClient(timeout=10.0) as client:
            response = await client.get(f"{self.base_url}/api/v1.0/notes/{note_id}")

        if response.status_code == 404:
            return None
        if response.status_code != 200:
            raise HTTPException(status_code=response.status_code, detail=response.text)

        return response.json()

    async def update(self, note_id: int, dto: Any) -> Optional[Dict[str, Any]]:
        payload = dto.model_dump(exclude_none=True, by_alias=True)

        async with httpx.AsyncClient(timeout=10.0) as client:
            response = await client.put(f"{self.base_url}/api/v1.0/notes/{note_id}", json=payload)

        if response.status_code == 404:
            return None
        if response.status_code != 200:
            raise HTTPException(status_code=response.status_code, detail=response.text)

        return response.json()

    async def delete(self, note_id: int) -> bool:
        async with httpx.AsyncClient(timeout=10.0) as client:
            response = await client.delete(f"{self.base_url}/api/v1.0/notes/{note_id}")

        if response.status_code == 404:
            return False
        if response.status_code not in (200, 204):
            raise HTTPException(status_code=response.status_code, detail=response.text)

        return True

    async def list_by_article_id(self, article_id: int) -> List[Dict[str, Any]]:
        async with httpx.AsyncClient(timeout=10.0) as client:
            response = await client.get(f"{self.base_url}/api/v1.0/notes/by-article/{article_id}")

        if response.status_code != 200:
            raise HTTPException(status_code=response.status_code, detail=response.text)

        return response.json()