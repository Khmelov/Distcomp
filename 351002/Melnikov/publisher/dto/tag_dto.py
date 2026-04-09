from pydantic import BaseModel, ConfigDict


class TagRequestTo(BaseModel):
    name: str


class TagResponseTo(BaseModel):
    id: int
    name: str

    model_config = ConfigDict(from_attributes=True)