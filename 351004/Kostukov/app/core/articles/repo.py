from threading import Lock
from typing import Dict, List, Optional
from app.core.articles.model import Article
from datetime import datetime

class InMemoryArticleRepo:
    def __init__(self):
        self._data: Dict[int, Article] = {}
        self._next_id = 1
        self._lock = Lock()

    def create(self, article: Article) -> Article:
        with self._lock:
            article.id = self._next_id
            if not isinstance(article.created, datetime):
                article.created = datetime.now()
            article.modified = article.created
            self._data[article.id] = article
            self._next_id += 1
            return article

    def get_by_id(self, id: int) -> Optional[Article]:
        return self._data.get(id)

    def update(self, id: int, article: Article) -> Article:
        with self._lock:
            if id not in self._data:
                raise KeyError("not found")
            existing = self._data[id]
            existing.title = article.title
            existing.content = article.content
            existing.writer_id = article.writer_id
            existing.marker_ids = list(article.marker_ids)
            existing.modified = article.modified
            return existing

    def delete(self, id: int) -> None:
        with self._lock:
            if id in self._data:
                del self._data[id]
            else:
                raise KeyError("not found")

    def list_all(self) -> List[Article]:
        return list(self._data.values())

    def list_filtered(self, predicate) -> List[Article]:
        return [a for a in self._data.values() if predicate(a)]
