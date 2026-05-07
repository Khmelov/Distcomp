from dataclasses import dataclass, field

from src.models.post_state import PostState


@dataclass(kw_only=True)
class Post:
    """Обсуждение к твиту. В Cassandra: PRIMARY KEY ((tweet_id), id); tweet_id — ключ партиции."""

    id: int = field(default=0, init=False)
    tweet_id: int
    content: str
    state: PostState = PostState.PENDING