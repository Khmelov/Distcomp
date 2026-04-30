from pydantic import BaseModel, Field

class PostRequestTo(BaseModel):
    articleId: int
    content: str = Field(..., min_length=2, max_length=2048)
    country: str = Field(default="BY")

class PostResponseTo(BaseModel):
    id: int
    articleId: int
    content: str
    state: str = "PENDING"