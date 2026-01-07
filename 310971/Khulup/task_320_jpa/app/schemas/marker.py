from pydantic import BaseModel, Field, ConfigDict

class MarkerBase(BaseModel):
    name: str = Field(min_length=2, max_length=32)

class MarkerCreate(MarkerBase):
    model_config = ConfigDict(extra="ignore")

class MarkerUpdate(MarkerBase):
    id: int

class MarkerRead(MarkerBase):
    model_config = ConfigDict(from_attributes=True)
    id: int
