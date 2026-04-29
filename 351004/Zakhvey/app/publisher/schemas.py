from pydantic import BaseModel, Field, ConfigDict
from datetime import datetime
from typing import List, Optional

# --- User DTOs ---
class UserRequestTo(BaseModel):
    login: str = Field(..., min_length=2, max_length=64)
    password: str = Field(..., min_length=8, max_length=128)
    firstname: str = Field(..., min_length=2, max_length=64)
    lastname: str = Field(..., min_length=2, max_length=64)

class UserResponseTo(BaseModel):
    id: int
    login: str
    firstname: str
    lastname: str
    model_config = ConfigDict(from_attributes=True)

# --- Label DTOs ---
class LabelRequestTo(BaseModel):
    name: str = Field(..., min_length=2, max_length=32)

class LabelResponseTo(BaseModel):
    id: int
    name: str
    model_config = ConfigDict(from_attributes=True)

# --- Issue DTOs ---
class IssueRequestTo(BaseModel):
    userId: int
    title: str = Field(..., min_length=2, max_length=64)
    content: str = Field(..., min_length=4, max_length=2048)
    labels: Optional[List[str]] = Field(default=[])  # <-- Безопасный дефолт

class ArticleResponseTo(BaseModel):
    id: int
    userId: int
    title: str
    content: str
    created: datetime
    modified: datetime
    model_config = ConfigDict(from_attributes=True)

# --- Comment DTOs ---
class CommentRequestTo(BaseModel):
    issueId: int
    content: str = Field(..., min_length=2, max_length=2048)

class CommentResponseTo(BaseModel):
    id: int
    issueId: int
    content: str
    model_config = ConfigDict(from_attributes=True)
