from typing import List, Optional
from datetime import datetime
import logging

from app.models.note import Note
from app.repositories.note_repository import NoteRepository
from app.schemas.note import NoteCreate, NoteUpdate, NoteRead


class NoteService:
    def __init__(self, repository: NoteRepository):
        self.repository = repository
    
    def create_note(self, note_create: NoteCreate) -> Note:
        try:
            if not note_create.content or len(note_create.content.strip()) < 2:
                raise ValueError("Content must be at least 2 characters long")
            
            if len(note_create.content) > 2048:
                raise ValueError("Content must be less than 2048 characters")
            
            return self.repository.create(note_create)
            
        except ValueError as e:
            logging.warning(f"Validation error creating note: {e}")
            raise
        except Exception as e:
            logging.error(f"Error creating note: {e}")
            raise
    
    def get_note_by_id(self, note_id: int, country: str = "US") -> Optional[Note]:
        try:
            return self.repository.get_by_id(note_id, country)
        except Exception as e:
            logging.error(f"Error getting note by id {note_id}: {e}")
            return None
    
    def get_notes_by_issue_id(self, issue_id: int) -> List[Note]:
        try:
            return self.repository.get_by_issue_id(issue_id)
        except Exception as e:
            logging.error(f"Error getting notes for issue {issue_id}: {e}")
            return []
    
    def update_note(self, note_update: NoteUpdate) -> Optional[Note]:
        try:
            if not note_update.content or len(note_update.content.strip()) < 2:
                raise ValueError("Content must be at least 2 characters long")
            
            if len(note_update.content) > 2048:
                raise ValueError("Content must be less than 2048 characters")
            
            existing_note = self.repository.get_by_id(note_update.id, note_update.country)
            if not existing_note:
                return None
            
            return self.repository.update(note_update)
            
        except ValueError as e:
            logging.warning(f"Validation error updating note: {e}")
            raise
        except Exception as e:
            logging.error(f"Error updating note: {e}")
            return None
    
    def delete_note(self, note_id: int, country: str = "US") -> bool:
        try:
            existing_note = self.repository.get_by_id(note_id, country)
            if not existing_note:
                return False
            
            return self.repository.delete(note_id, country)
            
        except Exception as e:
            logging.error(f"Error deleting note {note_id}: {e}")
            return False
    
    def note_to_read_schema(self, note: Note) -> NoteRead:
        return NoteRead(
            id=note.id,
            country=note.country,
            issueId=note.issueid,
            content=note.content,
            createdAt=note.created_at,
            updatedAt=note.updated_at
        )
