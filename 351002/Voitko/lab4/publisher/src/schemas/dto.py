from datetime import datetime
from typing import List, Optional

from pydantic import AliasChoices, BaseModel, Field


class WriterRequestTo(BaseModel):
    login: str = Field(..., min_length=2, max_length=64)
    password: str = Field(..., min_length=8, max_length=128)
    firstname: str = Field(..., min_length=2, max_length=64)
    lastname: str = Field(..., min_length=2, max_length=64)


class WriterResponseTo(BaseModel):
    id: int
    login: str
    firstname: str
    lastname: str


class NewsRequestTo(BaseModel):
    writerId: int
    title: str = Field(..., min_length=2, max_length=64)
    content: str = Field(..., min_length=4, max_length=2048)
    labelIds: Optional[List[int]] = None
    labelNames: Optional[List[str]] = Field(
        default=None,
        validation_alias=AliasChoices("labelNames", "labels", "label_names"),
    )


class NewsResponseTo(BaseModel):
    id: int
    writerId: int
    title: str
    content: str
    created: datetime
    modified: datetime


class LabelRequestTo(BaseModel):
    name: str = Field(..., min_length=2, max_length=32)


class LabelResponseTo(BaseModel):
    id: int
    name: str


class NoteRequestTo(BaseModel):
    newsId: int
    content: str = Field(..., min_length=2, max_length=2048)


class NoteResponseTo(BaseModel):
    id: int
    newsId: int
    content: str
