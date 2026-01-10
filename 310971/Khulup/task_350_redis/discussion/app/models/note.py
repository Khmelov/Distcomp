from dataclasses import dataclass
from datetime import datetime
from typing import Optional
from enum import Enum
import uuid

class NoteState(Enum):
    PENDING = "PENDING"
    APPROVE = "APPROVE"
    DECLINE = "DECLINE"

@dataclass
class Note:
    country: str
    issueid: int
    id: int
    content: str
    created_at: datetime
    updated_at: Optional[datetime] = None
    
    def to_dict(self):
        return {
            'country': self.country,
            'issueid': self.issueid,
            'id': self.id,
            'content': self.content,
            'created_at': self.created_at,
            'updated_at': self.updated_at
        }
    
    @classmethod
    def from_dict(cls, data):
        return cls(
            country=data.get('country'),
            issueid=data.get('issueid'),
            id=data.get('id'),
            content=data.get('content'),
            created_at=data.get('created_at'),
            updated_at=data.get('updated_at')
        )
