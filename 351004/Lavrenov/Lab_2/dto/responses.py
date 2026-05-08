from pydantic import BaseModel, Field
from typing import List, Optional
from datetime import datetime


# ----- User -----
class UserResponseTo(BaseModel):
    id: int
    login: str
    firstname: str
    lastname: str


# ----- Topic -----
class TopicResponseTo(BaseModel):
    id: int
    userId: int
    title: str
    content: str
    markerIds: List[int] = []
    created: Optional[datetime] = None
    modified: Optional[datetime] = None


# ----- Marker -----
class MarkerResponseTo(BaseModel):
    id: int
    name: str


# ----- Notice -----
class NoticeResponseTo(BaseModel):
    id: int
    topicId: int
    content: str
    created: Optional[datetime] = None
    modified: Optional[datetime] = None
