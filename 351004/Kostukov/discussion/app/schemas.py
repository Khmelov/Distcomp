from datetime import datetime
from enum import Enum
from typing import Annotated, Optional, List

from pydantic import BaseModel, ConfigDict, Field, StringConstraints


class NoteState(str, Enum):
    PENDING = "PENDING"
    APPROVE = "APPROVE"
    DECLINE = "DECLINE"


class NoteRequestTo(BaseModel):
    model_config = ConfigDict(populate_by_name=True, extra="ignore")

    article_id: int = Field(..., alias="articleId")
    content: Annotated[str, StringConstraints(min_length=2, max_length=2048)]


class NoteResponseTo(BaseModel):
    id: int
    article_id: int = Field(..., alias="articleId")
    content: str
    created_at: datetime = Field(..., alias="createdAt")
    state: Optional[NoteState] = None

    model_config = ConfigDict(populate_by_name=True, from_attributes=True)


class KafkaNoteRequest(BaseModel):
    model_config = ConfigDict(populate_by_name=True, extra="ignore")

    correlation_id: str = Field(alias="correlationId")
    action: str
    article_id: Optional[int] = Field(default=None, alias="articleId")
    note_id: Optional[int] = Field(default=None, alias="noteId")
    content: Optional[str] = None
    skip: int = 0
    limit: int = 10


class KafkaNoteResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True, extra="ignore")

    correlation_id: str = Field(alias="correlationId")
    status_code: int = Field(alias="statusCode")
    note: Optional[NoteResponseTo] = None
    notes: Optional[List[NoteResponseTo]] = None
    ok: Optional[bool] = None
    error: Optional[str] = None