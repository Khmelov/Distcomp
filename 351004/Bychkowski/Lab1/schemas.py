from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime

class WriterRequestTo(BaseModel):
    login: str = Field(..., min_length=2, max_length=64)
    password: str = Field(..., min_length=8, max_length=128)
    firstname: str = Field(..., min_length=2, max_length=64)
    lastname: str = Field(..., min_length=2, max_length=64)

class WriterResponseTo(BaseModel):
    id: int
    login: str
    firstname: str
    lastname: str

class LabelRequestTo(BaseModel):
    name: str = Field(..., min_length=2, max_length=32)

class LabelResponseTo(BaseModel):
    id: int
    name: str

class ArticleRequestTo(BaseModel):
    writerId: int
    title: str = Field(..., min_length=2, max_length=64)
    content: str = Field(..., min_length=4, max_length=2048)
    labelIds: List[int] = Field(default_factory=list)

class ArticleResponseTo(BaseModel):
    id: int
    writerId: int
    title: str
    content: str
    created: datetime
    modified: datetime
    labels: List[LabelResponseTo] = Field(default_factory=list)

class PostRequestTo(BaseModel):
    articleId: int
    content: str = Field(..., min_length=2, max_length=2048)

class PostResponseTo(BaseModel):
    id: int
    articleId: int
    content: str