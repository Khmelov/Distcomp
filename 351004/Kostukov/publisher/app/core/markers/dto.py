from pydantic import BaseModel, Field, StringConstraints
from typing import Optional, Annotated

class MarkerRequestTo(BaseModel):
    name: Annotated[str,StringConstraints(min_length=2, max_length=32)]

    class Config:
        json_schema_extra = {
            "example": {"name": "tutorial"}
        }

class MarkerRequestWrapper(BaseModel):
    marker: MarkerRequestTo

class MarkerResponseTo(BaseModel):
    id: int
    name: str

class MarkerResponseWrapper(BaseModel):
    marker: MarkerResponseTo
