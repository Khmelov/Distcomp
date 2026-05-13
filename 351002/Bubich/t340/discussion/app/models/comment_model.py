from dataclasses import dataclass
from datetime import datetime


@dataclass
class CommentModel:
    country: str
    story_id: int
    id: int
    content: str
    state: str = 'PENDING'  # PENDING, APPROVED, DECLINED

    def to_dict(self):
        return {
            'id': self.id,
            'storyId': self.story_id,
            'content': self.content,
            'state': self.state
        }