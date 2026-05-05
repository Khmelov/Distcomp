from fastapi import Depends
from motor.motor_asyncio import AsyncIOMotorCollection, AsyncIOMotorDatabase

from src.core.database import get_db
from src.services import NoteService

def get_notes_collection(
    db: AsyncIOMotorDatabase = Depends(get_db),
) -> AsyncIOMotorCollection:
    return db["tbl_note"]

def get_note_service(
        session: AsyncIOMotorCollection = Depends(get_notes_collection)
) -> NoteService:
    return NoteService(session)