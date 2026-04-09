from pydantic import BaseModel, ConfigDict, Field


class CommentRequestTo(BaseModel):
    issue_id: int = Field(alias="issueId")
    content: str

    model_config = ConfigDict(populate_by_name=True)


class CommentResponseTo(BaseModel):
    id: int
    issue_id: int = Field(alias="issueId")
    content: str

    model_config = ConfigDict(from_attributes=True, populate_by_name=True)