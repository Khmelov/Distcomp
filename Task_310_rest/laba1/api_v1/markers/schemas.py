from pydantic import BaseModel, ConfigDict


class Marker(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    name: str


class MarkerID(Marker):
    id: int