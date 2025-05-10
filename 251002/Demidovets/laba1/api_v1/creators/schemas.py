from pydantic import BaseModel, ConfigDict


class Editor(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    login: str 
    password: str
    firstname: str
    lastname: str


class EditorID(Editor):
    # model_config = ConfigDict(from_attributes=True)
    id: int 