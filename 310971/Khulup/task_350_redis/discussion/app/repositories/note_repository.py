from typing import List, Optional
from datetime import datetime
import uuid
import logging

from app.models.note import Note, NoteState
from app.core.cassandra import cassandra_config


class NoteRepository:
    def __init__(self, session=None):
        self.session = session or cassandra_config.get_session()
        self.table_name = "tbl_notes"
        self._create_table_if_not_exists()
        self._next_id = self._get_next_id()
    
    def _get_next_id(self):
        try:
            result = self.session.execute(f"SELECT id FROM {self.table_name}")
            existing_ids = []
            for row in result:
                try:
                    existing_ids.append(int(row.id))
                except ValueError:
                    continue
            
            if existing_ids:
                return max(existing_ids) + 1
            else:
                return 1
        except:
            return 1
    
    def _create_table_if_not_exists(self):
        try:
            query = f"""
                CREATE TABLE IF NOT EXISTS {self.table_name} (
                    country TEXT,
                    issueid BIGINT,
                    id BIGINT,
                    content TEXT,
                    created_at TIMESTAMP,
                    updated_at TIMESTAMP,
                    PRIMARY KEY ((country), issueid, id)
                );
            """
            self.session.execute(query)
            
            logging.info(f"Table {self.table_name} created or already exists")
        except Exception as e:
            logging.error(f"Error creating table {self.table_name}: {e}")
    
    def create(self, note_data) -> Note:
        try:
            note_id = self._next_id
            self._next_id += 1
            created_at = datetime.utcnow()
            
            note = Note(
                country=note_data.country,
                issueid=note_data.issueId,
                id=note_id,
                content=note_data.content,
                created_at=created_at,
                updated_at=None
            )
            
            query = f"""
                INSERT INTO {self.table_name} (country, issueid, id, content, created_at, updated_at)
                VALUES (%s, %s, %s, %s, %s, %s)
            """
            self.session.execute(query, (
                note.country,
                note.issueid,
                note.id,
                note.content,
                note.created_at,
                note.updated_at
            ))
            
            logging.info(f"Created note with id: {note.id}")
            return note
            
        except Exception as e:
            logging.error(f"Error creating note: {e}")
            raise e
    
    def get_by_id(self, note_id: int, country: str = "US") -> Optional[Note]:
        try:
            query = f"""
                SELECT country, issueid, id, content, created_at, updated_at 
                FROM {self.table_name} 
                WHERE id = %s ALLOW FILTERING
            """
            result = self.session.execute(query, (note_id,))
            row = result.one()
            
            if row:
                note = Note(
                    country=row.country,
                    issueid=row.issueid,
                    id=row.id,
                    content=row.content,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                logging.info(f"Retrieved note with id: {note_id}")
                return note
            else:
                logging.warning(f"Note with id {note_id} not found")
                return None
                
        except Exception as e:
            logging.error(f"Error getting note by id {note_id}: {e}")
            return None
    
    def get_by_issue_id(self, issue_id: int) -> List[Note]:
        try:
            query = f"""
                SELECT country, issueid, id, content, created_at, updated_at 
                FROM {self.table_name} 
                WHERE issueid = %s
            """
            result = self.session.execute(query, (issue_id,))
            
            notes = []
            for row in result:
                note = Note(
                    country=row.country,
                    issueid=row.issueid,
                    id=row.id,
                    content=row.content,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                notes.append(note)
            
            logging.info(f"Retrieved {len(notes)} notes for issueid: {issue_id}")
            return notes
            
        except Exception as e:
            logging.error(f"Error getting notes by issueid {issue_id}: {e}")
            return []
    
    def update(self, note_data) -> Optional[Note]:
        try:
            updated_at = datetime.utcnow()
            
            note = self.get_by_id(note_data.id, note_data.country)
            if not note:
                return None
            
            query = f"""
                UPDATE {self.table_name} 
                SET content = %s, updated_at = %s
                WHERE country = %s AND issueid = %s AND id = %s
            """
            self.session.execute(query, (
                note_data.content,
                updated_at,
                note_data.country,
                note_data.issueId,
                note_data.id
            ))
            
            updated_note = self.get_by_id(note_data.id, note_data.country)
            if updated_note:
                logging.info(f"Updated note with id: {note_data.id}")
                return updated_note
            else:
                logging.warning(f"Failed to retrieve updated note with id: {note_data.id}")
                return None
                
        except Exception as e:
            logging.error(f"Error updating note {note_data.id}: {e}")
            return None
    
    def delete(self, note_id: int, country: str = "US") -> bool:
        try:
            note = self.get_by_id(note_id, country)
            if not note:
                logging.warning(f"Note with id {note_id} not found for deletion")
                return False
            
            query = f"""
                DELETE FROM {self.table_name} 
                WHERE country = %s AND issueid = %s AND id = %s
            """
            self.session.execute(query, (note.country, note.issueid, note_id))
            
            logging.info(f"Deleted note with id: {note_id}")
            return True
            
        except Exception as e:
            logging.error(f"Error deleting note {note_id}: {e}")
            return False
    
    def get_all(self) -> List[Note]:
        try:
            query = f"""
                SELECT country, issueid, id, content, created_at, updated_at 
                FROM {self.table_name}
            """
            result = self.session.execute(query)
            
            notes = []
            for row in result:
                note = Note(
                    country=row.country,
                    issueid=row.issueid,
                    id=row.id,
                    content=row.content,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                notes.append(note)
            
            logging.info(f"Retrieved {len(notes)} notes")
            return notes
            
        except Exception as e:
            logging.error(f"Error getting all notes: {e}")
            return []
