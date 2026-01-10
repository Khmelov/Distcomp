from __future__ import annotations

from datetime import datetime
from typing import List, Optional

from pydantic import BaseModel, ConfigDict, Field


class BaseDTO(BaseModel):
    model_config = ConfigDict(from_attributes=True)


class CreatorRequest(BaseDTO):
    login: str
    password: str
    firstname: str
    lastname: str


class CreatorResponse(CreatorRequest):
    id: int


class CreatorUpdate(CreatorRequest):
    id: int


class ArticleRequest(BaseDTO):
    creatorId: int
    title: str
    content: str
    tagIds: Optional[List[int]] = Field(default_factory=list)
    tagNames: Optional[List[str]] = Field(default_factory=list)


class ArticleResponse(ArticleRequest):
    id: int
    created: datetime
    modified: datetime
    tagIds: List[int] = Field(default_factory=list)
    tagNames: List[str] = Field(default_factory=list)


class ArticleUpdate(BaseModel):
    id: int
    creatorId: int
    title: str
    content: str
    tagIds: Optional[List[int]] = Field(default_factory=list)
    tagNames: Optional[List[str]] = Field(default_factory=list)


class TagRequest(BaseDTO):
    name: str


class TagResponse(TagRequest):
    id: int


class TagUpdate(BaseModel):
    id: int
    name: str


class MessageRequest(BaseDTO):
    articleId: int
    content: str


class MessageResponse(MessageRequest):
    id: int


class MessageUpdate(BaseModel):
    id: int
    articleId: int
    content: str
