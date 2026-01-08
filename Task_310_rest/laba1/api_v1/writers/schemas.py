from pydantic import BaseModel, ConfigDict


class Writer(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    login: str 
    password: str
    firstname: str
    lastname: str


class WriterID(Writer):
    # model_config = ConfigDict(from_attributes=True)
    id: int 