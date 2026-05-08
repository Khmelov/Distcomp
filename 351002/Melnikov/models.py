from dataclasses import dataclass, field
from datetime import datetime, timezone

def utc_now():
    return datetime.now(timezone.utc)

@dataclass
class Author:
    id: int = None
    login: str = ""
    password: str = ""
    firstname: str = ""
    lastname: str = ""

@dataclass
class Issue:
    id: int = None
    authorId: int = None
    title: str = ""
    content: str = ""
    created: datetime = field(default_factory=utc_now)
    modified: datetime = field(default_factory=utc_now)
    tagIds: list[int] = field(default_factory=list)

@dataclass
class Tag:
    id: int = None
    name: str = ""

@dataclass
class Comment:
    id: int = None
    issueId: int = None
    content: str = ""