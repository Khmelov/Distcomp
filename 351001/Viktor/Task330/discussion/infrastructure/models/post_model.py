# discussion/src/infrastructure/models/post_model.py
from dataclasses import dataclass

@dataclass
class Post:
    """Модель поста для Cassandra."""
    tweet_id: int       # ключ партиционирования (issueId в терминах Java)
    id: int             # кластеризационный ключ
    content: str
    # country: str      # если нужно, добавьте как обычное поле