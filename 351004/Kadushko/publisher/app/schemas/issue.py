from pydantic import BaseModel, Field, field_validator, ConfigDict, field_serializer
from typing import Optional, List
from datetime import datetime


class IssueBase(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    editor_id: int = Field(alias="editorId")
    title: str
    content: str

    @field_validator("title")
    @classmethod
    def validate_title(cls, v: str) -> str:
        if not (2 <= len(v) <= 64):
            raise ValueError("title must be between 2 and 64 characters")
        return v

    @field_validator("content")
    @classmethod
    def validate_content(cls, v: str) -> str:
        if not (4 <= len(v) <= 2048):
            raise ValueError("content must be between 4 and 2048 characters")
        return v


class IssueCreate(IssueBase):
    markers: Optional[List[str]] = None


class IssueUpdate(IssueBase):
    id: Optional[int] = None


class IssueResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)

    id: int
    editor_id: int = Field(serialization_alias="editorId")
    title: str
    content: str
    created: datetime
    modified: datetime

    @field_serializer("created", "modified")
    def serialize_dt(self, v: datetime) -> str:
        return v.isoformat()