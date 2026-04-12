import httpx
from typing import List, Optional
from schemas.note import NoteRequestTo, NoteResponseTo


class DiscussionClient:
    def __init__(self):
        self.base_url = "http://localhost:24130/api/v1.0"

    async def create_note(self, dto: NoteRequestTo) -> Optional[NoteResponseTo]:
        async with httpx.AsyncClient() as client:
            response = await client.post(f"{self.base_url}/notes", json=dto.model_dump())
            if response.status_code == 201:
                return NoteResponseTo(**response.json())
            return None

    async def get_notes_by_issue(self, issue_id: int) -> List[NoteResponseTo]:
        async with httpx.AsyncClient() as client:
            response = await client.get(f"{self.base_url}/issues/{issue_id}/notes")
            if response.status_code == 200:
                return [NoteResponseTo(**item) for item in response.json()]
            return []

    async def get_note(self, issue_id: int, note_id: int) -> Optional[NoteResponseTo]:
        async with httpx.AsyncClient() as client:
            response = await client.get(f"{self.base_url}/issues/{issue_id}/notes/{note_id}")
            if response.status_code == 200:
                return NoteResponseTo(**response.json())
            return None

    async def update_note(self, issue_id: int, note_id: int, dto: NoteRequestTo) -> Optional[NoteResponseTo]:
        async with httpx.AsyncClient() as client:
            response = await client.put(
                f"{self.base_url}/issues/{issue_id}/notes/{note_id}",
                json=dto.model_dump()
            )
            if response.status_code == 200:
                return NoteResponseTo(**response.json())
            return None

    async def delete_note(self, issue_id: int, note_id: int) -> bool:
        async with httpx.AsyncClient() as client:
            response = await client.delete(f"{self.base_url}/issues/{issue_id}/notes/{note_id}")
            return response.status_code == 204

    async def update_note_state(self, issue_id: int, note_id: int, state: str) -> bool:
        async with httpx.AsyncClient() as client:
            response = await client.patch(
                f"{self.base_url}/issues/{issue_id}/notes/{note_id}/state",
                json={"state": state}
            )
            return response.status_code == 200