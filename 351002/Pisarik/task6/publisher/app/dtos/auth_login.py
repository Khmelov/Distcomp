from pydantic import BaseModel, ConfigDict, Field, constr


class LoginRequestTo(BaseModel):
    model_config = ConfigDict(str_strip_whitespace=True)

    login: constr(min_length=2, max_length=64)
    password: constr(min_length=1, max_length=128)


class TokenResponseTo(BaseModel):
    access_token: str
    token_type: str = "Bearer"
