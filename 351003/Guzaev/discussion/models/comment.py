from dataclasses import dataclass

@dataclass
class Comment:
    id: int
    tweet_id: int
    country: str
    content: str