from pydantic import BaseModel, ConfigDict

class AuthroBase(BaseModel):
    login: str
    password: str
    firstname: str
    lastname: str

    model_config = ConfigDict(from_attributes=True)

class AuthorRequestTo(AuthroBase):
    pass

class AuthorResponseTo(AuthroBase):
    id: int