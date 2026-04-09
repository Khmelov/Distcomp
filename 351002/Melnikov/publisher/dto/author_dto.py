from pydantic import BaseModel, ConfigDict


class AuthorRequestTo(BaseModel):
    login: str
    password: str
    firstname: str
    lastname: str


class AuthorResponseTo(BaseModel):
    id: int
    login: str
    password: str
    firstname: str
    lastname: str

    model_config = ConfigDict(from_attributes=True)