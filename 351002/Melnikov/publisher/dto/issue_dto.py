from datetime import datetime
from pydantic import BaseModel, ConfigDict, Field


class IssueRequestTo(BaseModel):
    author_id: int = Field(alias="authorId")
    title: str
    content: str
    tag_ids: list[int] = Field(default_factory=list, alias="tagIds")

    model_config = ConfigDict(populate_by_name=True)


class IssueResponseTo(BaseModel):
    id: int
    author_id: int = Field(alias="authorId")
    title: str
    content: str
    created: datetime
    modified: datetime
    tag_ids: list[int] = Field(default_factory=list, alias="tagIds")

    model_config = ConfigDict(from_attributes=True, populate_by_name=True)