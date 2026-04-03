from datetime import datetime

from pydantic import BaseModel, Field


class TweetRequestTo(BaseModel):
    editor_id: int = Field(alias="editorId")
    title: str = Field(min_length=2, max_length=64)
    content: str = Field(min_length=4, max_length=2048)
    markers: list[str] = Field(default_factory=list)


class TweetResponseTo(BaseModel):
    id: int
    editor_id: int = Field(serialization_alias="editorId")
    title: str
    content: str
    created: datetime
    modified: datetime
    markers: list[str] = Field(default_factory=list)

    model_config = {"from_attributes": True}
