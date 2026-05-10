from dataclasses import dataclass

@dataclass
class Message:
    id: int
    newsId: int
    content: str
