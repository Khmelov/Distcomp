from dataclasses import dataclass, field


@dataclass(kw_only=True)
class Post:
    id: int = field(default=0, init=False)
    tweet_id: int
    content: str