from dataclasses import dataclass

@dataclass
class Comment:
    id: int = 0
    tweet_id: int = 0
    content: str = ""
