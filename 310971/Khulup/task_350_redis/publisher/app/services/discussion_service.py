import httpx
import logging
from typing import List, Optional, Dict, Any
from app.schemas.note import NoteCreate, NoteRead, NoteUpdate
from app.core.cache_decorators import cache_result, invalidate_cache_pattern

class DiscussionService:
    
    def __init__(self):
        self.base_url = "http://localhost:24130/api/v1.0"
        self.timeout = 30.0
    
    @invalidate_cache_pattern("note")
    async def create_note(self, note_data: NoteCreate) -> Optional[NoteRead]:
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.post(
                    f"{self.base_url}/notes",
                    json={
                        "issueId": note_data.issueId,
                        "content": note_data.content
                    }
                )
                
                if response.status_code == 201:
                    data = response.json()
                    return NoteRead(
                        id=int(data["id"]),
                        country=data["country"],
                        issueId=data["issueId"],
                        content=data["content"],
                        createdAt=data["createdAt"],
                        updatedAt=data.get("updatedAt")
                    )
                else:
                    logging.error(f"Failed to create note: {response.status_code} - {response.text}")
                    return None
                    
        except Exception as e:
            logging.error(f"Error creating note: {e}")
            return None
    
    @cache_result("note", ttl=1800)
    async def get_note_by_id(self, note_id: str) -> Optional[NoteRead]:
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.get(f"{self.base_url}/notes/{note_id}")
                
                if response.status_code == 200:
                    data = response.json()
                    return NoteRead(
                        id=int(data["id"]),
                        issueId=data["issueId"],
                        content=data["content"],
                        createdAt=data["createdAt"],
                        updatedAt=data.get("updatedAt")
                    )
                elif response.status_code == 404:
                    return None
                else:
                    logging.error(f"Failed to get note: {response.status_code} - {response.text}")
                    return None
                    
        except Exception as e:
            logging.error(f"Error getting note {note_id}: {e}")
            return None
    
    @cache_result("note_list", ttl=900)
    async def get_notes_by_issue_id(self, issue_id: int) -> List[NoteRead]:
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.get(
                    f"{self.base_url}/notes",
                    params={"issue_id": issue_id}
                )
                
                if response.status_code == 200:
                    data = response.json()
                    return [
                        NoteRead(
                            id=int(note["id"]),
                            issueId=note["issueId"],
                            content=note["content"],
                            createdAt=note["createdAt"],
                            updatedAt=note.get("updatedAt")
                        )
                        for note in data
                    ]
                else:
                    logging.error(f"Failed to get notes: {response.status_code} - {response.text}")
                    return []
                    
        except Exception as e:
            logging.error(f"Error getting notes for issue {issue_id}: {e}")
            return []
    
    @invalidate_cache_pattern("note")
    async def update_note(self, note_data: NoteUpdate) -> Optional[NoteRead]:
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.put(
                    f"{self.base_url}/notes",
                    json={
                        "id": note_data.id,
                        "issueId": note_data.issueId,
                        "content": note_data.content
                    }
                )
                
                if response.status_code == 200:
                    data = response.json()
                    return NoteRead(
                        id=int(data["id"]),
                        issueId=data["issueId"],
                        content=data["content"],
                        createdAt=data["createdAt"],
                        updatedAt=data.get("updatedAt")
                    )
                elif response.status_code == 404:
                    return None
                else:
                    logging.error(f"Failed to update note: {response.status_code} - {response.text}")
                    return None
                    
        except Exception as e:
            logging.error(f"Error updating note: {e}")
            return None
    
    @invalidate_cache_pattern("note")
    async def delete_note(self, note_id: str) -> bool:
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.delete(f"{self.base_url}/notes/{note_id}")
                
                if response.status_code == 204:
                    return True
                elif response.status_code == 404:
                    return False
                else:
                    logging.error(f"Failed to delete note: {response.status_code} - {response.text}")
                    return False
                    
        except Exception as e:
            logging.error(f"Error deleting note {note_id}: {e}")
            return False
    
    async def health_check(self) -> Dict[str, Any]:
        try:
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.get(f"{self.base_url}/health")
                
                if response.status_code == 200:
                    return response.json()
                else:
                    return {"status": "unhealthy", "error": f"HTTP {response.status_code}"}
                    
        except Exception as e:
            logging.error(f"Error checking discussion health: {e}")
            return {"status": "unhealthy", "error": str(e)}
