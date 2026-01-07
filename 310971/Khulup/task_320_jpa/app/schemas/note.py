from pydantic import BaseModel, Field, ConfigDict


class NoteCreate(BaseModel):
    issueId: int
    content: str = Field(min_length=2, max_length=2048)

class NoteRead(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)
    id: int
    issueId: int
    content: str

class NoteUpdate(BaseModel):
    id: int
    issueId: int
    content: str = Field(min_length=2, max_length=2048)
