from datetime import datetime
from pydantic import BaseModel, ConfigDict, Field


class LoginRequestTo(BaseModel):
    login: str
    password: str

class TokenResponseTo(BaseModel):
    access_token: str
    token_type: str = "bearer"


class EditorRequestTo(BaseModel):
    login: str = Field(min_length=2, max_length=64)
    password: str = Field(min_length=8, max_length=128)
    firstname: str = Field(min_length=2, max_length=64)
    lastname: str = Field(min_length=2, max_length=64)
    role: str = "CUSTOMER"


class EditorResponseTo(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    login: str
    firstname: str
    lastname: str
    role: str = "CUSTOMER"


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
    issue_id: int = Field(alias="issueId")
    state: str = "PENDING"


class IssueRequestTo(BaseModel):
    title: str = Field(min_length=2, max_length=64)
    content: str = Field(min_length=2, max_length=2048)
    editor_id: int = Field(alias="editorId")
    label_ids: list[int] = Field(default_factory=list, alias="labelIds")
    labels: list[str] = Field(default_factory=list)


class IssueResponseTo(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)
    id: int
    title: str
    content: str
    created: datetime
    modified: datetime
    editor_id: int = Field(serialization_alias="editorId")