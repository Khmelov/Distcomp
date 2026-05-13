from dataclasses import dataclass
from typing import Optional

@dataclass
class CommentResponseTo:
    id: int
    story_id: Optional[int]
    content: str