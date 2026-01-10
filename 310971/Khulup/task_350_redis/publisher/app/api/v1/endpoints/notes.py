from fastapi import APIRouter, HTTPException, Query, status, Depends
from typing import List, Optional
import logging
import uuid
from pydantic import BaseModel

from app.schemas.note import NoteCreate, NoteRead, NoteUpdate
from app.services.discussion_service import DiscussionService
from app.services.kafka_service import PublisherKafkaService
from app.core.db import SessionLocal
from app.repositories.issue_repository import IssueRepository
from app.services.issue_service import IssueService

router = APIRouter()

kafka_service = PublisherKafkaService()
logging.info("Publisher Kafka service initialized globally")

def get_discussion_service():
    return DiscussionService()

def get_kafka_service():
    return kafka_service

@router.get("", response_model=List[NoteRead])
async def get_notes(
    issue_id: Optional[int] = Query(None, description="ID Issue для фильтрации заметок"),
    discussion_service: DiscussionService = Depends(get_discussion_service)
):
    try:
        if issue_id is not None:
            notes = await discussion_service.get_notes_by_issue_id(issue_id)
        else:
            notes = []
        
        return notes
        
    except Exception as e:
        logging.error(f"Error getting notes: {e}")
        raise HTTPException(
            status_code=500,
            detail="Internal server error"
        )

@router.post("", response_model=NoteRead, status_code=status.HTTP_201_CREATED)
async def create_note(
    note_data: NoteCreate,
    kafka_service: PublisherKafkaService = Depends(get_kafka_service)
):
    session = SessionLocal()
    try:
        issue_repo = IssueRepository(session)
        issue_service = IssueService(issue_repo)
        issue = issue_service.get_issue(note_data.issueId)
        if not issue:
            raise HTTPException(status_code=400, detail="Issue with this id does not exist")

        request_id = str(uuid.uuid4())
        
        note_dict = {
            'issueId': note_data.issueId,
            'content': note_data.content
        }
        
        success = kafka_service.send_note_request(note_dict, request_id)
        
        if not success:
            raise HTTPException(
                status_code=500,
                detail="Failed to send note request"
            )
        
        response = kafka_service.wait_for_response(request_id, timeout=30.0)
        
        if response and response.get('success'):
            note_data = response['data']
            return NoteRead(
                id=note_data['id'],
                issueId=note_data['issueId'],
                content=note_data['content'],
                createdAt=note_data['createdAt'],
                updatedAt=note_data.get('updatedAt')
            )
        else:
            error_msg = response.get('message', 'Unknown error') if response else 'Timeout'
            raise HTTPException(
                status_code=500,
                detail=f"Failed to create note: {error_msg}"
            )
        
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error creating note: {e}")
        raise HTTPException(
            status_code=500,
            detail="Internal server error"
        )
    finally:
        session.close()

@router.get("/{note_id}", response_model=NoteRead)
async def get_note_by_id(
    note_id: str,
    discussion_service: DiscussionService = Depends(get_discussion_service)
):
    try:
        try:
            note_id_int = int(note_id)
        except (ValueError, TypeError):
            raise HTTPException(
                status_code=400,
                detail="Invalid note ID format, must be integer"
            )
        
        note = await discussion_service.get_note_by_id(str(note_id_int))
        if not note:
            raise HTTPException(
                status_code=404,
                detail="Note not found"
            )
        return note
        
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error getting note {note_id}: {e}")
        raise HTTPException(
            status_code=500,
            detail="Internal server error"
        )

@router.put("", response_model=NoteRead)
async def update_note(
    note_data: NoteUpdate,
    discussion_service: DiscussionService = Depends(get_discussion_service)
):
    try:
        note = await discussion_service.update_note(note_data)
        if not note:
            raise HTTPException(
                status_code=404,
                detail="Note not found"
            )
        return note
        
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error updating note: {e}")
        raise HTTPException(
            status_code=500,
            detail="Internal server error"
        )

@router.put("/{note_id}", response_model=NoteRead)
async def update_note_by_id(
    note_id: str,
    note_data: dict,
    discussion_service: DiscussionService = Depends(get_discussion_service)
):
    try:
        try:
            note_id_int = int(note_id)
        except (ValueError, TypeError):
            raise HTTPException(
                status_code=400,
                detail="Invalid note ID format, must be integer"
            )
        
        update_data = NoteUpdate(
            id=note_id_int,
            issueId=note_data.get("issueId"),
            content=note_data.get("content")
        )
        note = await discussion_service.update_note(update_data)
        if not note:
            raise HTTPException(
                status_code=404,
                detail="Note not found"
            )
        return note
        
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error updating note {note_id}: {e}")
        raise HTTPException(
            status_code=500,
            detail="Internal server error"
        )

@router.delete("/{note_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_note(
    note_id: str,
    discussion_service: DiscussionService = Depends(get_discussion_service)
):
    try:
        try:
            note_id_int = int(note_id)
        except (ValueError, TypeError):
            raise HTTPException(
                status_code=400,
                detail="Invalid note ID format, must be integer"
            )
        
        success = await discussion_service.delete_note(str(note_id_int))
        if not success:
            raise HTTPException(
                status_code=404,
                detail="Note not found"
            )
        
        return None
        
    except HTTPException:
        raise
    except Exception as e:
        logging.error(f"Error deleting note {note_id}: {e}")
        raise HTTPException(
            status_code=500,
            detail="Internal server error"
        )
