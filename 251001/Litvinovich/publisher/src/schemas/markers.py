from pydantic import BaseModel, Field

from src.models.models import Marker


class MarkerBase(BaseModel):
    name: str = Field(min_length=2, max_length=32)

    model_config = {
        'from_attributes': True,
    }

class MarkerRequestToAdd(MarkerBase):
    pass

class MarkerRequestToUpdate(MarkerRequestToAdd):
    id: int

class MarkerResponseTo(MarkerBase):
    id: int
