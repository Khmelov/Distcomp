from enum import Enum

class ReactionState(str, Enum):
    PENDING = "PENDING"
    APPROVE = "APPROVE"
    DECLINE = "DECLINE"

def moderate(content: str) -> ReactionState:
    stop_words = {"spam", "bad", "inappropriate"}
    if any(word in content.lower() for word in stop_words):
        return ReactionState.DECLINE
    return ReactionState.APPROVE