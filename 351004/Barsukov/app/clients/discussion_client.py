import httpx
import json
from typing import List, Optional
from schemas.note import NoteRequestTo, NoteResponseTo
from redis_config import redis_client  # Импортируем наш Redis


class DiscussionClient:
    def __init__(self):
        self.base_url = "http://localhost:24130/api/v1.0"
        self.cache_prefix = "note:"

    async def get_note(self, issue_id: int, note_id: int) -> Optional[NoteResponseTo]:
        cache_key = f"{self.cache_prefix}{issue_id}:{note_id}"

        # 1. Проверяем кеш Redis
        cached = await redis_client.get(cache_key)
        if cached:
            print(f"--- Cache hit for note {issue_id}:{note_id} ---")
            return NoteResponseTo(**json.loads(cached))

        # 2. Если нет в кеше — идем в сервис Discussion
        async with httpx.AsyncClient() as client:
            response = await client.get(f"{self.base_url}/issues/{issue_id}/notes/{note_id}")
            if response.status_code == 200:
                note_data = response.json()
                note = NoteResponseTo(**note_data)

                # 3. Сохраняем в кеш на 10 минут
                await redis_client.set(cache_key, note.model_dump_json(), ex=600)
                return note
            return None

    async def create_note(self, dto: NoteRequestTo) -> Optional[NoteResponseTo]:
        async with httpx.AsyncClient() as client:
            response = await client.post(f"{self.base_url}/notes", json=dto.model_dump())
            if response.status_code == 201:
                return NoteResponseTo(**response.json())
            return None

    async def update_note(self, issue_id: int, note_id: int, dto: NoteRequestTo) -> Optional[NoteResponseTo]:
        async with httpx.AsyncClient() as client:
            response = await client.put(
                f"{self.base_url}/issues/{issue_id}/notes/{note_id}",
                json=dto.model_dump()
            )
            if response.status_code == 200:
                # Инвалидация кеша при обновлении
                await redis_client.delete(f"{self.cache_prefix}{issue_id}:{note_id}")
                return NoteResponseTo(**response.json())
            return None

    async def delete_note(self, issue_id: int, note_id: int) -> bool:
        async with httpx.AsyncClient() as client:
            response = await client.delete(f"{self.base_url}/issues/{issue_id}/notes/{note_id}")
            if response.status_code == 204:
                # Удаляем из кеша
                await redis_client.delete(f"{self.cache_prefix}{issue_id}:{note_id}")
                return True
            return False

    async def update_note_state(self, issue_id: int, note_id: int, state: str) -> bool:
        async with httpx.AsyncClient() as client:
            response = await client.patch(
                f"{self.base_url}/issues/{issue_id}/notes/{note_id}/state",
                json={"state": state}
            )
            if response.status_code == 200:
                # Обязательно удаляем кеш, так как статус (state) изменился (был PENDING, стал APPROVE)
                await redis_client.delete(f"{self.cache_prefix}{issue_id}:{note_id}")
                return True
            return False

    async def get_notes_by_issue(self, issue_id: int) -> List[NoteResponseTo]:
        # Список заметок обычно не кешируют или кешируют сложнее, оставим так
        async with httpx.AsyncClient() as client:
            response = await client.get(f"{self.base_url}/issues/{issue_id}/notes")
            if response.status_code == 200:
                return [NoteResponseTo(**item) for item in response.json()]
            return []
