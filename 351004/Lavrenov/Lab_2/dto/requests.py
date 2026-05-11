from pydantic import BaseModel, Field, constr
from typing import List, Optional


# ----- User -----
class UserRequestTo(BaseModel):
    login: constr(min_length=2, max_length=64)
    password: constr(min_length=8, max_length=128)
    firstname: constr(min_length=2, max_length=64)
    lastname: constr(min_length=2, max_length=64)


# ----- Topic -----
class TopicRequestTo(BaseModel):
    userId: int = Field(..., gt=0)
    title: constr(min_length=2, max_length=64)
    content: constr(min_length=4, max_length=2048)
    markerIds: Optional[List[int]] = Field(default_factory=list)
    markers: Optional[List[str]] = Field(default_factory=list)


# ----- Marker -----
class MarkerRequestTo(BaseModel):
    name: constr(min_length=2, max_length=32)


# ----- Notice -----
class NoticeRequestTo(BaseModel):
    topicId: int = Field(..., gt=0)
    content: constr(min_length=4, max_length=2048)
