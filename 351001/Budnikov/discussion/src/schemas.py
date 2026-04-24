from pydantic import BaseModel, ConfigDict, Field


class PostRequestTo(BaseModel):
    content: str = Field(min_length=2, max_length=2048)
    issue_id: int | None = Field(default=None, alias="issueId")


class PostResponseTo(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)
    id: int
    content: str
    issue_id: int = Field(alias="issueId")