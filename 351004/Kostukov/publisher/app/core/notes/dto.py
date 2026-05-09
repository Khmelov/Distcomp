from pydantic import BaseModel, Field, constr, ConfigDict, StringConstraints
from datetime import datetime
from typing import Annotated

class NoteRequestTo(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    article_id: int = Field(..., alias="articleId")
    content: Annotated[str,StringConstraints(min_length=2, max_length=2048)]

class NoteResponseTo(BaseModel):
    id: int
    article_id: int = Field(..., alias="articleId")
    content: str
    created_at: datetime = Field(..., alias="createdAt")

    model_config = ConfigDict(validate_by_name=True)
