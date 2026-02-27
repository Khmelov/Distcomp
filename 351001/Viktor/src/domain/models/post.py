from dataclasses import dataclass


@dataclass
class Post:
    id: int
    content: str
    tweet_id: int
