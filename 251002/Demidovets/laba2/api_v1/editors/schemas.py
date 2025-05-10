from pydantic import BaseModel, ConfigDict, Field


class Editor(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    login: str = Field(min_length=2, max_length=64)
    password: str = Field(min_length=8, max_length=128)
    firstname: str = Field(min_length=2, max_length=64)
    lastname: str = Field(min_length=2, max_length=64)


class EditorID(Editor):
    # model_config = ConfigDict(from_attributes=True)
    id: int 