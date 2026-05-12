from typing import List
from pydantic import BaseModel, constr, Field

class NewsRequestTo(BaseModel):
    authorId: int
    title: constr(min_length=2, max_length=64)
    content: constr(min_length=4, max_length=2048)
    markIds: List[int] = Field(default_factory=list)
