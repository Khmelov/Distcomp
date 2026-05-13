from typing import Literal, Optional

from pydantic import AliasChoices, BaseModel, ConfigDict, Field


class AuthorRegisterV2To(BaseModel):
    """Registration body: tests send firstname/lastname (v1 style) or firstName/lastName (camelCase)."""

    model_config = ConfigDict(str_strip_whitespace=True)

    login: str = Field(min_length=2, max_length=64)
    password: str = Field(min_length=8, max_length=128)
    firstname: str = Field(
        min_length=2,
        max_length=64,
        validation_alias=AliasChoices("firstName", "firstname"),
    )
    lastname: str = Field(
        min_length=2,
        max_length=64,
        validation_alias=AliasChoices("lastName", "lastname"),
    )
    role: Optional[Literal["ADMIN", "CUSTOMER"]] = Field(default="CUSTOMER")
