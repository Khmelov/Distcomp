from datetime import datetime
from pydantic import BaseModel, ConfigDict, Field


class EditorRequestTo(BaseModel):
    login: str = Field(min_length=2, max_length=64)
    password: str = Field(min_length=8, max_length=128)
    firstname: str = Field(min_length=2, max_length=64)
    lastname: str = Field(min_length=2, max_length=64)


class EditorResponseTo(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    login: str
    firstname: str
    lastname: str


class LabelRequestTo(BaseModel):
    name: str = Field(min_length=2, max_length=32)


class LabelResponseTo(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    name: str


class PostRequestTo(BaseModel):
    content: str = Field(min_length=2, max_length=2048)
    issue_id: int = Field(alias="issueId")


class PostResponseTo(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)
    id: int
    content: str
    issue_id: int = Field(serialization_alias="issueId")


class IssueRequestTo(BaseModel):
    title: str = Field(min_length=2, max_length=64)
    content: str = Field(min_length=2, max_length=2048)
    editor_id: int = Field(alias="editorId")
    label_ids: list[int] = Field(default_factory=list, alias="labelIds")


class IssueResponseTo(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)
    id: int
    title: str
    content: str
    created: datetime
    modified: datetime
    editor_id: int = Field(serialization_alias="editorId")