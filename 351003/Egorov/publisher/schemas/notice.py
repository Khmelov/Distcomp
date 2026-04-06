from pydantic import BaseModel, Field

from publisher.schemas.common import Timestamped


class NoticeBase(BaseModel):
    content: str = Field(..., min_length=1)
    story_id: int


class NoticeCreate(NoticeBase):
    pass


class NoticeUpdate(NoticeBase):
    pass


class NoticeRead(Timestamped):
    id: int
    content: str
    story_id: int

    class Config:
        from_attributes = True

