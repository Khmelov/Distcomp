from pydantic import BaseModel, constr, StringConstraints
from typing import Annotated

class WriterRequestTo(BaseModel):
    login: Annotated[str, StringConstraints(min_length=2, max_length=64)]
    password: Annotated[str, StringConstraints(min_length=8, max_length=128)]
    firstname: Annotated[str, StringConstraints(min_length=2, max_length=64)]
    lastname: Annotated[str, StringConstraints(min_length=2, max_length=64)]

class WriterResponseTo(BaseModel):
    id: int
    login: str
    firstname: str
    lastname: str

class WriterRequestWrapper(BaseModel):
    writer: WriterRequestTo

class WriterResponseWrapper(BaseModel):
    writer: WriterResponseTo