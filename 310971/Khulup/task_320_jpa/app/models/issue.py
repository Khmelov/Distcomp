from pydantic import BaseModel, ConfigDict, Field
from datetime import datetime

class Issue(BaseModel):
    model_config = ConfigDict(from_attributes=True, populate_by_name=True)
    id: int
    userId: int = Field(alias="user_id")
    title: str
    content: str
    created: datetime
    modified: datetime
