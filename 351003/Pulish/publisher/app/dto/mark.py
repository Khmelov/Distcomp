from pydantic import BaseModel, Field


class MarkRequestTo(BaseModel):
    id: int | None = None
    name: str = Field(min_length=1)


class MarkResponseTo(BaseModel):
    id: int
    name: str
