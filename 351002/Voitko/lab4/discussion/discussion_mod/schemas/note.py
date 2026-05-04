from pydantic import BaseModel, Field


class NoteRequestTo(BaseModel):
    newsId: int
    content: str = Field(..., min_length=2, max_length=2048)


class NoteResponseTo(BaseModel):
    id: int
    newsId: int
    content: str
