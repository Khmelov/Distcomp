from pydantic import BaseModel, ConfigDict, Field

from src.domain.models.models import UserRole


class AuthroBase(BaseModel):
    login: str = Field(min_length=2, max_length=64)
    password: str = Field(min_length=8, max_length=128)
    firstname: str = Field(min_length=2, max_length=64)
    lastname: str = Field(min_length=2, max_length=64)
    role: UserRole = Field(default=UserRole.CUSTOMER)

    model_config = ConfigDict(from_attributes=True)

class AuthorRequestTo(AuthroBase):
    pass

class AuthorResponseTo(AuthroBase):
    id: int