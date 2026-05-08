from dataclasses import dataclass


@dataclass
class Note:
    id: int
    content: str
    topic_id: int
