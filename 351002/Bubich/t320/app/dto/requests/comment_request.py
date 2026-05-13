from dataclasses import dataclass

@dataclass
class CommentRequestTo:
    story_id: int = 0
    content: str = ""