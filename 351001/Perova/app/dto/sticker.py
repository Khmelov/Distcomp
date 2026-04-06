from pydantic import BaseModel, Field


class StickerRequestTo(BaseModel):
    id: int | None = None
    name: str = Field(min_length=2, max_length=32)


class StickerResponseTo(BaseModel):
    id: int
    name: str
