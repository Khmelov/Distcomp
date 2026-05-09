from typing import List, Any

from fastapi import HTTPException
from httpx import AsyncClient, Response

from src.schemas.note import NoteResponseTo, NoteRequestTo

class NoteService:
    def __init__(self, http_client: AsyncClient):
        self.client = http_client

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

    async def get_one(self, note_id: int) -> NoteResponseTo:
        response = await self.client.get(f"notes/{note_id}")
        return await self._handle_response(response)

    async def get_all(self) -> List[NoteResponseTo]:
        response = await self.client.get(f"notes")
        return await self._handle_response(response)
    
    async def create(self, dto: NoteRequestTo) -> NoteResponseTo:
        response = await self.client.post(f"notes", json=dto.model_dump())
        return await self._handle_response(response)

    async def update(self, note_id: int, dto: NoteRequestTo) -> NoteResponseTo:
        response = await self.client.put(f"notes/{note_id}", json=dto.model_dump())
        return await self._handle_response(response)

    async def delete(self, note_id: int) -> None:
        await self.client.delete(f"notes/{note_id}")