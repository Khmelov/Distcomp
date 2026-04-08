from fastapi import APIRouter, status, Depends, HTTPException
from schemas.note import NoteRequestTo, NoteResponseTo
from clients.discussion_client import DiscussionClient
from kafka_producer import kafka_producer
from kafka_config import IN_TOPIC

router = APIRouter()
discussion_client = DiscussionClient()


@router.post("", response_model=NoteResponseTo, status_code=status.HTTP_201_CREATED)
async def create_note(dto: NoteRequestTo):
    # Сначала создаем заметку через REST в discussion
    note = await discussion_client.create_note(dto)
    if not note:
        raise HTTPException(status_code=500, detail="Failed to create note")

    # Отправляем в Kafka для модерации
    await kafka_producer.send_message(
        IN_TOPIC,
        key=str(note.issueId),  # Ключ - issueId для гарантии попадания в одну партицию
        value=note.model_dump()
    )

    return note