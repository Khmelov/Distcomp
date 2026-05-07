from pydantic import BaseModel, Field

from publisher.schemas.common import Timestamped


class MarkerBase(BaseModel):
    name: str = Field(..., min_length=1, max_length=255)


class MarkerCreate(MarkerBase):
    pass


class MarkerUpdate(MarkerBase):
    pass


class MarkerRead(Timestamped):
    id: int
    name: str

    class Config:
        from_attributes = True

