from pydantic import BaseModel, Field, field_validator, ConfigDict
from typing import Optional


class CommentBase(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    issue_id: int = Field(alias="issueId")
    content: str

    @field_validator("content")
    @classmethod
    def validate_content(cls, v: str) -> str:
        if not (2 <= len(v) <= 2048):
            raise ValueError("content must be between 2 and 2048 characters")
        return v


class CommentCreate(CommentBase):
    pass


class CommentUpdate(CommentBase):
    id: Optional[int] = None


class CommentResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)

    id: int
    issue_id: int = Field(serialization_alias="issueId")
    content: str