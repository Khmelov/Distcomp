from pydantic import BaseModel, Field, ConfigDict
from typing import Optional, List
from datetime import datetime

class AuthorRequestTo(BaseModel):
    model_config = ConfigDict(title="author")
    login: str = Field(..., min_length=2, max_length=64)
    password: str = Field(..., min_length=8, max_length=128)
    firstname: str = Field(..., min_length=2, max_length=64)
    lastname: str = Field(..., min_length=2, max_length=64)

class AuthorResponseTo(AuthorRequestTo):
    id: int

class TagRequestTo(BaseModel):
    model_config = ConfigDict(title="tag")
    name: str = Field(..., min_length=2, max_length=32)

class TagResponseTo(TagRequestTo):
    id: int

class IssueRequestTo(BaseModel):
    model_config = ConfigDict(title="issue")
    authorId: int
    title: str = Field(..., min_length=2, max_length=64)
    content: str = Field(..., min_length=4, max_length=2048)
    tagIds: Optional[List[int]] = Field(default_factory=list)

class IssueResponseTo(IssueRequestTo):
    id: int
    created: datetime
    modified: datetime

class CommentRequestTo(BaseModel):
    model_config = ConfigDict(title="comment")
    issueId: int
    content: str = Field(..., min_length=2, max_length=2048)

class CommentResponseTo(CommentRequestTo):
    id: int