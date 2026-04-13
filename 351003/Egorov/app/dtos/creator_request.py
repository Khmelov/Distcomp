from pydantic import BaseModel, EmailStr, constr


class CreatorRequestTo(BaseModel):
    login: constr(min_length=1, max_length=50)
    name: constr(min_length=1, max_length=100)
    email: EmailStr

