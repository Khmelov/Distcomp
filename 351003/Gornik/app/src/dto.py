from datetime import datetime

from pydantic import BaseModel, Field

class TweetRequestTo(BaseModel):
    title: str = Field(..., min_length=2, max_length=64, example="Мой первый твит")
    content: str = Field(..., min_length=4, max_length=2048, example="Содержание твита")
    writerId: int = Field(..., ge=1, example=1)

class TweetResponseTo(BaseModel):
    id: int
    title: str
    content: str
    created_at: datetime
    modified_at: datetime
    writerId: int

    class Config:
        orm_mode = True


class WriterRequestTo(BaseModel):
    login: str = Field(..., min_length=2, max_length=64, example="email")
    password: str = Field(..., min_length=8, max_length=128, example="1234")
    firstname: str = Field(..., min_length=2, max_length=64, example="Egor")
    lastname: str = Field(..., min_length=2, max_length=64, example="Antipov")

class WriterResponseTo(BaseModel):
    id: int
    login: str
    password: str
    firstname: str
    lastname: str

    class Config:
        orm_mode = True


class CommentRequestTo(BaseModel):
    tweetId: int = Field(..., ge=1)
    content: str = Field(..., min_length=2, max_length=2048, example="Сontent")

class CommentResponseTo(BaseModel):
    id: int
    tweetId: int
    content: str

    class Config:
        orm_mode = True

class StickerRequestTo(BaseModel):
    name: str = Field(..., min_length=2, max_length=32, example="Sticker")

class StickerResponseTo(BaseModel):
    id: int
    name: str

    class Config:
        orm_mode = True