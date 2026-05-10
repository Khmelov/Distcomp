from dataclasses import dataclass

@dataclass
class Comment:
    id: int = 0
    tweet_id: int = 0
    content: str = ""
    country: str = "Belarus"
    state: str = "PENDING"  # PENDING | APPROVE | DECLINE