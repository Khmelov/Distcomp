from pydantic import AliasChoices, BaseModel, ConfigDict, Field

from app.models.user_role import UserRole


class UserRequestTo(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    id: int | None = None
    login: str = Field(min_length=2, max_length=64)
    password: str = Field(min_length=8, max_length=128)
    firstName: str = Field(
        min_length=2,
        max_length=64,
        validation_alias=AliasChoices("firstName", "firstname"),
    )
    lastName: str = Field(
        min_length=2,
        max_length=64,
        validation_alias=AliasChoices("lastName", "lastname"),
    )
    role: UserRole = UserRole.CUSTOMER


class UserResponseTo(BaseModel):
    id: int
    login: str
    password: str
    firstname: str
    lastname: str
    role: UserRole
