from dataclasses import dataclass
from enum import Enum

class PostState(str, Enum):
    PENDING = "PENDING"
    APPROVE = "APPROVE"
    DECLINE = "DECLINE"

@dataclass
class Post:
    tweet_id: int
    id: int
    content: str
    state: PostState = PostState.PENDING   # новое поле