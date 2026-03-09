from typing import List

from sqlalchemy.ext.asyncio import AsyncSession

from src.domain.repositories.postgresql import NoteRepository
from src.domain.models import Note
from src.schemas.note import NoteResponseTo, NoteRequestTo


class NoteService:
    def __init__(self, session: AsyncSession):
        self.session = session
        self.note_repo = NoteRepository(session)

    async def get_one(self, note_id: int) -> NoteResponseTo:
        note = await self.note_repo.get_one(note_id)
        return NoteResponseTo.model_validate(note)
    
    async def get_all(self) -> List[Note]:
        return await self.note_repo.get_all()
    
    async def create(self, dto: NoteRequestTo) -> NoteResponseTo:
        note_args = dto.model_dump()
        created_note = await self.note_repo.create(**note_args)
        await self.session.commit()
        return NoteResponseTo.model_validate(created_note)

    async def update(self, note_id: int, dto: NoteRequestTo) -> NoteResponseTo:
        note_args = dto.model_dump()
        updated_note = await self.note_repo.update(note_id, **note_args)
        await self.session.commit()
        return NoteResponseTo.model_validate(updated_note)

    async def delete(self, note_id: int) -> None:
        await self.note_repo.delete(note_id)
        await self.session.commit()