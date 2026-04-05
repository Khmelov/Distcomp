from pydantic import BaseModel, Field, field_validator, ConfigDict
from typing import Optional


class EditorBase(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    login: str
    password: str
    firstname: str
    lastname: str

    @field_validator("login")
    @classmethod
    def validate_login(cls, v: str) -> str:
        if not (2 <= len(v) <= 64):
            raise ValueError("login must be between 2 and 64 characters")
        return v

    @field_validator("password")
    @classmethod
    def validate_password(cls, v: str) -> str:
        if not (8 <= len(v) <= 128):
            raise ValueError("password must be between 8 and 128 characters")
        return v

    @field_validator("firstname", "lastname")
    @classmethod
    def validate_name(cls, v: str) -> str:
        if not (2 <= len(v) <= 64):
            raise ValueError("name must be between 2 and 64 characters")
        return v


class EditorCreate(EditorBase):
    pass


class EditorUpdate(EditorBase):
    id: Optional[int] = None


class EditorResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    login: str
    firstname: str
    lastname: str