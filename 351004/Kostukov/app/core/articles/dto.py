from pydantic import BaseModel, conlist, Field, StringConstraints
from typing import List, Optional, Annotated
from datetime import datetime

class ArticleRequestTo(BaseModel):
    writer_id: int = Field(..., alias="writerId")
    title: Annotated[str,StringConstraints(min_length=2, max_length=64)]
    content: Annotated[str, StringConstraints(min_length=2, max_length=2048)]
    marker_ids: Optional[conlist(int, min_length=0)] = Field(default=None, alias="markerIds")

    class Config:
        validate_by_name=True
        json_schema_extra = {
            "example": {
                "writerId": 1,
                "title": "My article",
                "content": "Some long content ...",
                "markerIds": [1, 2]
            }
        }

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
    marker_list: List[MarkerShortTo] = Field(default_factory=list, alias="markers")
    created: datetime
    modified: datetime

    class Config:
        validate_by_name = True

class ArticleResponseWrapper(BaseModel):
    article: ArticleResponseTo
