from src.models.post_state import PostState

_STOP_WORDS = frozenset(
    {
        "spam",
        "scam",
        "реклама",
        "запрещено",
        "offensive",
    }
)


def moderate_content(content: str) -> PostState:
    lower = content.lower()
    if any(w in lower for w in _STOP_WORDS):
        return PostState.DECLINE
    return PostState.APPROVE
