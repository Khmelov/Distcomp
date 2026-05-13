from dataclasses import dataclass
from typing import Optional
from datetime import datetime
import uuid


@dataclass
class CommentModel:
    """Модель комментария для Cassandra"""
    country: str  # partition key - распределяет данные по нодам
    story_id: int  # clustering key
    id: int  # clustering key
    content: str

    def to_dict(self):
        return {
            'id': self.id,
            'storyId': self.story_id,
            'content': self.content
        }

    @staticmethod
    def from_dict(data: dict):
        return CommentModel(
            country=data.get('country', 'default'),
            story_id=data.get('story_id', data.get('storyId', 0)),
            id=data.get('id', 0),
            content=data.get('content', '')
        )