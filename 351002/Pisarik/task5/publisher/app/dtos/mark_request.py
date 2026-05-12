from pydantic import BaseModel, constr

class MarkRequestTo(BaseModel):
    name: constr(min_length=2, max_length=32)
