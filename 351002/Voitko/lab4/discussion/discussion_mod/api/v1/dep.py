from typing import Annotated

from fastapi import Depends

from discussion_mod.core.database import get_session
from discussion_mod.services.note_service import NoteService


def get_note_service() -> NoteService:
    return NoteService(get_session())


NoteServiceDep = Annotated[NoteService, Depends(get_note_service)]
