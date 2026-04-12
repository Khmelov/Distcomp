from pydantic import BaseModel, EmailStr, Field, ConfigDict
from typing import Optional
from datetime import datetime

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

# --- Issue DTOs ---
class IssueRequestTo(BaseModel):
    userId: int
    title: str = Field(..., min_length=2, max_length=64)
    content: str = Field(..., min_length=4, max_length=2048)
    labelIds: list[int] = []

class ArticleResponseTo(BaseModel):
    id: int
    userId: int
    title: str
    content: str
    created: datetime
    modified: datetime

# --- Label DTOs ---
class LabelRequestTo(BaseModel):
    name: str = Field(..., min_length=2, max_length=32)

class LabelResponseTo(BaseModel):
    id: int
    name: str

# --- Comment DTOs ---
class CommentRequestTo(BaseModel):
    issueId: int
    content: str = Field(..., min_length=2, max_length=2048)

class CommentResponseTo(BaseModel):
    id: int
    issueId: int
    content: str