from pydantic import BaseModel
from enum import Enum

class ReactionState(str, Enum):
    PENDING = "PENDING"
    APPROVE = "APPROVE"
    DECLINE = "DECLINE"

class ReactionBase(BaseModel):
    id: int
    content: str
    state: ReactionState
    news_id: int

class ReactionUpdate(BaseModel):
    state: ReactionState

class Reaction(ReactionBase):
    class Config:
        from_attributes = True