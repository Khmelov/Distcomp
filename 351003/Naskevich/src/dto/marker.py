from pydantic import BaseModel, Field


class MarkerRequestTo(BaseModel):
    name: str = Field(min_length=2, max_length=32)


class MarkerResponseTo(BaseModel):
    id: int
    name: str

    model_config = {"from_attributes": True}
