from typing import Optional, List
from app.models.note import Note
from app.schemas.note import NoteCreate
from app.repositories.base_repository import BaseRepository
from app.models.entities import NoteEntity
from app.core.db import SessionLocal
from sqlalchemy.orm import Session
from sqlalchemy.exc import IntegrityError
from psycopg2.errors import ForeignKeyViolation

class NoteRepository(BaseRepository[Note]):
    def __init__(self, session: Optional[Session] = None):
        self.session = session or SessionLocal()

    def add(self, note_create: NoteCreate) -> Note:
        note_entity = NoteEntity(
            issue_id=note_create.issueId,
            content=note_create.content
        )
        self.session.add(note_entity)
        try:
            self.session.commit()
            self.session.refresh(note_entity)
            return Note.model_validate(note_entity)
        except IntegrityError as e:
            self.session.rollback()
            if isinstance(e.orig, ForeignKeyViolation):
                raise ValueError("Issue with this id does not exist")
            raise e

    def get_by_id(self, note_id: int) -> Optional[Note]:
        note_entity = self.session.query(NoteEntity).filter(NoteEntity.id == note_id).first()
        return Note.model_validate(note_entity) if note_entity else None

    def list_notes(self, issueId: int | None = None, limit: int = 50, offset: int = 0,
                   sort_by: str | None = None, sort_dir: str = "desc") -> List[Note]:
        query = self.session.query(NoteEntity)
        
        if issueId:
            query = query.filter(NoteEntity.issue_id == issueId)
        
        if sort_by:
            if sort_by == "issueId":
                sort_col = NoteEntity.issue_id
            else:
                sort_col = getattr(NoteEntity, sort_by, NoteEntity.id)
            if sort_dir == "desc":
                query = query.order_by(sort_col.desc())
            else:
                query = query.order_by(sort_col.asc())
        else:
            query = query.order_by(NoteEntity.id.desc())
        
        query = query.offset(offset).limit(limit)
        notes = query.all()
        return [Note.model_validate(n) for n in notes]

    def list(self) -> List[Note]:
        notes = self.session.query(NoteEntity).all()
        return [Note.model_validate(n) for n in notes]

    def delete(self, note_id: int) -> bool:
        note_entity = self.session.query(NoteEntity).filter(NoteEntity.id == note_id).first()
        if note_entity:
            self.session.delete(note_entity)
            self.session.commit()
            return True
        return False

    def update(self, updated_note: Note) -> Optional[Note]:
        note_entity = self.session.query(NoteEntity).filter(NoteEntity.id == updated_note.id).first()
        if note_entity:
            note_entity.issue_id = updated_note.issueId
            note_entity.content = updated_note.content
            self.session.commit()
            self.session.refresh(note_entity)
            return Note.model_validate(note_entity)
        return None
