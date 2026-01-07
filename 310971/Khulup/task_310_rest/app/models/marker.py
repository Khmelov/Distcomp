from pydantic import BaseModel

class Marker(BaseModel):
    id: int
    name: str
