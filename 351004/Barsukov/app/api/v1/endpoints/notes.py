from fastapi import APIRouter, status, Depends, HTTPException
from schemas.note import NoteRequestTo, NoteResponseTo
from clients.discussion_client import DiscussionClient
from kafka_producer import kafka_producer
from kafka_config import IN_TOPIC

router = APIRouter()
discussion_client = DiscussionClient()

@router.post("", response_model=NoteResponseTo, status_code=status.HTTP_201_CREATED)
async def create_note(dto: NoteRequestTo):
    note = await discussion_client.create_note(dto)
    if not note:
        raise HTTPException(status_code=500, detail="Failed to create note")

    await kafka_producer.send_message(
        IN_TOPIC,
        key=str(note.issueId),
        value=note.model_dump()
    )
    return note

# Добавим получение конкретной заметки для проверки кеша в DiscussionClient
@router.get("/{issue_id}/{note_id}", response_model=NoteResponseTo)
async def get_note(issue_id: int, note_id: int):
    note = await discussion_client.get_note(issue_id, note_id)
    if not note:
        raise HTTPException(404, "Note not found")
    return note