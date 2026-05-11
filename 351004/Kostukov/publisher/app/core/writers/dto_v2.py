from datetime import datetime
from typing import Literal, Optional

from pydantic import BaseModel, ConfigDict, Field, StringConstraints
from typing import Annotated


class WriterRegisterRequestTo(BaseModel):
    model_config = ConfigDict(populate_by_name=True, extra="ignore")

    login: Annotated[str, StringConstraints(min_length=3, max_length=64)]
    password: Annotated[str, StringConstraints(min_length=6, max_length=128)]
    first_name: str = Field(..., alias="firstName")
    last_name: str = Field(..., alias="lastName")
    role: Literal["ADMIN", "CUSTOMER"]


class WriterLoginRequestTo(BaseModel):
    model_config = ConfigDict(populate_by_name=True, extra="ignore")

    login: Annotated[str, StringConstraints(min_length=3, max_length=64)]
    password: Annotated[str, StringConstraints(min_length=6, max_length=128)]


class WriterResponseTo(BaseModel):
    id: int
    login: str
    first_name: Optional[str] = Field(None, alias="firstName")
    last_name: Optional[str] = Field(None, alias="lastName")
    role: str
    created_at: Optional[datetime] = Field(None, alias="createdAt")

    model_config = ConfigDict(populate_by_name=True)

    class TokenResponseTo(BaseModel):
        access_token: str
        token_type: str = "Bearer"