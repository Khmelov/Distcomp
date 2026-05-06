from enum import Enum
from uuid import uuid4
from pydantic import BaseModel, Field, ConfigDict
from typing import Optional


class NoteState(str, Enum):
    PENDING = "PENDING"
    APPROVE = "APPROVE"
    DECLINE = "DECLINE"


class NoteKafkaRequest(BaseModel):
    model_config = ConfigDict(populate_by_name=True, extra="ignore")

    correlation_id: str = Field(default_factory=lambda: str(uuid4()), alias="correlationId")
    article_id: int = Field(alias="articleId")
    content: str
    state: NoteState = NoteState.PENDING


class NoteKafkaResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True, extra="ignore")

    correlation_id: str = Field(alias="correlationId")
    note_id: Optional[int] = Field(default=None, alias="id")
    article_id: int = Field(alias="articleId")
    content: str
    state: NoteState
    created_at: Optional[str] = Field(default=None, alias="createdAt")
    error: Optional[str] = None