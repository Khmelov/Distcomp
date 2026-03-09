from dataclasses import dataclass, field
from datetime import datetime

@dataclass
class Writer:
    id: int
    login: str
    password: str
    firstname: str
    lastname: str

@dataclass
class Article:
    id: int
    writer_id: int
    title: str
    content: str
    created: datetime
    modified: datetime

@dataclass
class Label:
    id: int
    name: str

@dataclass
class Post:
    id: int
    article_id: int
    content: str