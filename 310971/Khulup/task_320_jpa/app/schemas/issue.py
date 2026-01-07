from pydantic import BaseModel, Field, ConfigDict
from datetime import datetime

class IssueCreate(BaseModel):
    userId: int
    title: str = Field(..., min_length=2, max_length=64)
    content: str = Field(..., min_length=4, max_length=2048)

class IssueUpdate(BaseModel):
    id: int
    userId: int
    title: str = Field(..., min_length=2, max_length=64)
    content: str = Field(..., min_length=4, max_length=2048)

class IssueRead(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)
    id: int
    userId: int
    title: str
    content: str
    created: datetime
    modified: datetime
