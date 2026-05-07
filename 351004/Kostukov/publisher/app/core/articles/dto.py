from pydantic import BaseModel, conlist, Field, StringConstraints, ConfigDict
from typing import List, Optional, Annotated
from datetime import datetime

class ArticleRequestTo(BaseModel):
    writer_id: int = Field(..., alias="writerId")
    title: Annotated[str, StringConstraints(min_length=2, max_length=64)]
    content: Annotated[str, StringConstraints(min_length=2, max_length=2048)]
    markers: Optional[list[str]] = None

    model_config = ConfigDict(populate_by_name=True)

class ArticleRequestWrapper(BaseModel):
    article: ArticleRequestTo

class MarkerShortTo(BaseModel):
    id: int
    name: str

class ArticleResponseTo(BaseModel):
    id: int
    writer_id: int = Field(..., alias="writerId")
    title: str
    content: str
    markers: List[MarkerShortTo] = Field(default_factory=list, alias="markers")
    created: datetime
    modified: datetime

    model_config = ConfigDict(populate_by_name=True)

class ArticleResponseWrapper(BaseModel):
    article: ArticleResponseTo
