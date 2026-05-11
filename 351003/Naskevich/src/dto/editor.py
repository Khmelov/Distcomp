from pydantic import BaseModel, ConfigDict, Field

from src.models.user_role import UserRole


class EditorRequestTo(BaseModel):
    login: str = Field(min_length=2, max_length=64)
    password: str = Field(min_length=8, max_length=128)
    firstname: str = Field(min_length=2, max_length=64)
    lastname: str = Field(min_length=2, max_length=64)
    role: UserRole | None = None


class EditorOut(BaseModel):
    id: int
    login: str
    firstname: str
    lastname: str
    role: UserRole

    model_config = ConfigDict(from_attributes=True)


EditorResponseTo = EditorOut


class EditorRegisterV2(BaseModel):
    login: str = Field(min_length=2, max_length=64)
    password: str = Field(min_length=8, max_length=128)
    firstname: str = Field(min_length=2, max_length=64, alias="firstName")
    lastname: str = Field(min_length=2, max_length=64, alias="lastName")
    role: UserRole

    model_config = ConfigDict(populate_by_name=True)
