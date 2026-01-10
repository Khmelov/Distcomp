from __future__ import annotations

from typing import List, Optional

from pydantic import BaseModel, ConfigDict, Field


class _BaseDTO(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)


class CreatorRequestTo(_BaseDTO):
    login: str
    password: str
    firstname: str
    lastname: str


class CreatorResponseTo(CreatorRequestTo):
    id: int


class ArticleRequestTo(_BaseDTO):
    creatorId: int
    title: str
    content: str
    tagIds: Optional[List[int]] = None


class ArticleResponseTo(ArticleRequestTo):
    id: int
    created: str
    modified: str
    tagIds: List[int] = Field(default_factory=list)


class TagRequestTo(_BaseDTO):
    name: str


class TagResponseTo(TagRequestTo):
    id: int


class MessageRequestTo(_BaseDTO):
    articleId: int
    content: str


class MessageResponseTo(MessageRequestTo):
    id: int
