from pydantic import BaseModel
from datetime import datetime

class Issue(BaseModel):
    id: int
    userId: int
    title: str
    content: str
    created: datetime
    modified: datetime
