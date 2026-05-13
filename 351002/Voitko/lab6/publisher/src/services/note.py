import json
from typing import List, Any

from fastapi import HTTPException
from httpx import AsyncClient, Response
from redis.asyncio import Redis

from src.schemas.note import NoteResponseTo, NoteRequestTo

class NoteService:
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

    def _get_cache_key(self, writer_id: int) -> str:
        return f"note:{writer_id}"

    async def get_one(self, note_id: int) -> NoteResponseTo:
        cache_note = await self.redis.get(self._get_cache_key(note_id))
        if cache_note:
            data = json.loads(cache_note)
            return NoteResponseTo.model_validate(data)

        response = await self.client.get(f"notes/{note_id}")
        note_data = await self._handle_response(response)
        note_response = NoteResponseTo.model_validate(note_data)
        await self.redis.set(self._get_cache_key(note_id), json.dumps(note_response.model_dump(), default=str))
        return note_response

    async def get_all(self) -> List[NoteResponseTo]:
        response = await self.client.get(f"notes")
        return await self._handle_response(response)
    
    async def create(self, dto: NoteRequestTo) -> NoteResponseTo:
        response = await self.client.post(f"notes", json=dto.model_dump())
        note_data = await self._handle_response(response)
        note_response = NoteResponseTo.model_validate(note_data)
        await self.redis.set(self._get_cache_key(note_response.id), json.dumps(note_response.model_dump(), default=str))
        return note_response

    async def update(self, note_id: int, dto: NoteRequestTo) -> NoteResponseTo:
        response = await self.client.put(f"notes/{note_id}", json=dto.model_dump())
        note_data = await self._handle_response(response)
        note_response = NoteResponseTo.model_validate(note_data)
        await self.redis.set(self._get_cache_key(note_id), json.dumps(note_response.model_dump(), default=str))
        return note_response

    async def delete(self, note_id: int) -> None:
        await self.redis.delete(self._get_cache_key(note_id))
        await self.client.delete(f"notes/{note_id}")