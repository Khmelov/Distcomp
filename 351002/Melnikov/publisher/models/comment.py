from dataclasses import dataclass


@dataclass
class Comment:
    id: int
    issue_id: int
    content: str