from pydantic import BaseModel, constr

class AuthorRequestTo(BaseModel):
    login: constr(min_length=2, max_length=64)
    password: constr(min_length=8, max_length=128)
    firstname: constr(min_length=2, max_length=64)
    lastname: constr(min_length=2, max_length=64)
