from pydantic import BaseModel, Field, field_validator, ConfigDict
from typing import Optional


class MarkerBase(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    name: str

    @field_validator("name")
    @classmethod
    def validate_name(cls, v: str) -> str:
        if not (2 <= len(v) <= 32):
            raise ValueError("name must be between 2 and 32 characters")
        return v


class MarkerCreate(MarkerBase):
    pass


class MarkerUpdate(MarkerBase):
    id: Optional[int] = None


class MarkerResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    name: str