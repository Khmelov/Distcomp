from dataclasses import dataclass, field
from datetime import datetime

@dataclass
class User:
    id: int
    login: str
    password: str
    firstname: str
    lastname: str

@dataclass
class Issue:
    id: int
    userId: int
    title: str
    content: str
    created: datetime
    modified: datetime

@dataclass
class Label:
    id: int
    name: str

@dataclass
class Comment:
    id: int
    issueId: int
    content: str

@dataclass
class IssueLabel:
    id: int
    issueId: int
    labelId: int