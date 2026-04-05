from typing import List

from motor.motor_asyncio import AsyncIOMotorCollection

from src.domain.repositories import NoteRepository
from src.schemas.note import NoteResponseTo, NoteRequestTo


class NoteService:
    def __init__(self, collection: AsyncIOMotorCollection):
        self.note_repo = NoteRepository(collection)

    async def get_one(self, note_id: int) -> NoteResponseTo:
        note = await self.note_repo.get_one(note_id)
        return NoteResponseTo.model_validate(note)

    async def get_all(self) -> List[NoteResponseTo]:
        notes = await self.note_repo.get_all()
        return [NoteResponseTo.model_validate(note) for note in notes]

    async def create(self, dto: NoteRequestTo) -> NoteResponseTo:
        note_args = dto.model_dump()
        created_note = await self.note_repo.create(**note_args)
        return NoteResponseTo.model_validate(created_note)

    async def update(self, note_id: int, dto: NoteRequestTo) -> NoteResponseTo:
        note_args = dto.model_dump()
        updated_note = await self.note_repo.update(note_id, **note_args)
        return NoteResponseTo.model_validate(updated_note)

    async def delete(self, note_id: int) -> None:
        await self.note_repo.delete(note_id)