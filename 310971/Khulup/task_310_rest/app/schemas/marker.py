from pydantic import BaseModel, Field

class MarkerBase(BaseModel):
    name: str = Field(..., min_length=2, max_length=32)

class MarkerCreate(MarkerBase):
    pass

class MarkerUpdate(MarkerBase):
    id: int

class MarkerRead(MarkerBase):
    id: int
