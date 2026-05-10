import json
from typing import List, Any

from fastapi import HTTPException
from httpx import AsyncClient, Response
from redis.asyncio import Redis

from src.schemas.news import NewsResponseTo, NewsRequestTo

class NewsService:
    def __init__(self, http_client: AsyncClient, redis: Redis) -> None:
        self.client = http_client
        self.redis = redis

    async def _handle_response(self, response: Response) -> Any:
        if response.is_error:
            try:
                detail = response.json().get("detail", "Service error")
            except:
                detail = response.text
            raise HTTPException(status_code=response.status_code, detail=detail)

        if response.status_code == 204 or not response.content:
            return None

        return response.json()

    def _get_cache_key(self, user_id: int) -> str:
        return f"news:{user_id}"

    async def get_one(self, news_id: int) -> NewsResponseTo:
        cache_news = await self.redis.get(self._get_cache_key(news_id))
        if cache_news:
            data = json.loads(cache_news)
            return NewsResponseTo.model_validate(data)

        response = await self.client.get(f"newss/{news_id}")
        news_data = await self._handle_response(response)
        news_response = NewsResponseTo.model_validate(news_data)
        await self.redis.set(self._get_cache_key(news_id), json.dumps(news_response.model_dump(), default=str))
        return news_response

    async def get_all(self) -> List[NewsResponseTo]:
        response = await self.client.get(f"newss")
        return await self._handle_response(response)
    
    async def create(self, dto: NewsRequestTo) -> NewsResponseTo:
        response = await self.client.post(f"newss", json=dto.model_dump())
        news_data = await self._handle_response(response)
        news_response = NewsResponseTo.model_validate(news_data)
        await self.redis.set(self._get_cache_key(news_response.id), json.dumps(news_response.model_dump(), default=str))
        return news_response

    async def update(self, news_id: int, dto: NewsRequestTo) -> NewsResponseTo:
        response = await self.client.put(f"newss/{news_id}", json=dto.model_dump())
        news_data = await self._handle_response(response)
        news_response = NewsResponseTo.model_validate(news_data)
        await self.redis.set(self._get_cache_key(news_id), json.dumps(news_response.model_dump(), default=str))
        return news_response

    async def delete(self, news_id: int) -> None:
        await self.redis.delete(self._get_cache_key(news_id))
        await self.client.delete(f"newss/{news_id}")