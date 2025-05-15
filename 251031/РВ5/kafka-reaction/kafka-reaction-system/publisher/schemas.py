from pydantic import BaseModel
from enum import Enum

class ReactionState(str, Enum):
    PENDING = "PENDING"
    APPROVE = "APPROVE"
    DECLINE = "DECLINE"

class ReactionBase(BaseModel):
    content: str
    state: ReactionState = ReactionState.PENDING
    news_id: int

class ReactionCreate(ReactionBase):
    pass

class Reaction(ReactionBase):
    id: int

    class Config:
        from_attributes = True