from pydantic import BaseModel, ConfigDict, Field


class Marker(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    name: str = Field(min_length=2, max_length=32)


class MarkerID(Marker):
    id: int