from pydantic import BaseModel, Field, ConfigDict

class Note(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)
    id: int
    issueId: int = Field(alias="issue_id")
    content: str = Field(max_length=2048)
