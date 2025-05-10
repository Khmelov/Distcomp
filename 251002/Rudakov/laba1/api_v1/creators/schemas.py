from pydantic import BaseModel, ConfigDict


class Creator(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    login: str 
    password: str
    firstname: str
    lastname: str


class CreatorID(Creator):
    # model_config = ConfigDict(from_attributes=True)
    id: int 