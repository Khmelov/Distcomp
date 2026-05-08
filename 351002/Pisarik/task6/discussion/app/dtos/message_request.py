from typing import Optional

from pydantic import BaseModel, constr


class MessageRequestTo(BaseModel):
    newsId: int
    content: constr(min_length=2, max_length=2048)
    country: Optional[str] = None
