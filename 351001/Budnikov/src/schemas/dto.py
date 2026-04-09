from pydantic import BaseModel, ConfigDict, Field


class EditorRequestTo(BaseModel):
    login: str = Field(min_length=2, max_length=64)
    password: str = Field(min_length=8, max_length=128)
    firstname: str = Field(min_length=2, max_length=64)
    lastname: str = Field(min_length=2, max_length=64)


class EditorResponseTo(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    login: str
    firstname: str
    lastname: str
