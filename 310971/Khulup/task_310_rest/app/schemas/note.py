from pydantic import BaseModel, Field


class NoteCreate(BaseModel):
    issueId: int
    content: str = Field(min_length=2, max_length=2048)

class NoteRead(BaseModel):
    id: int
    issueId: int
    content: str
