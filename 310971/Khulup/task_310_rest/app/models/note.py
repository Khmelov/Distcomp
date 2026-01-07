from pydantic import BaseModel, Field

class Note(BaseModel):
    id: int
    issueId: int
    content: str = Field(max_length=2048)
