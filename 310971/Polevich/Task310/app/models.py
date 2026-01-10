from __future__ import annotations

from dataclasses import dataclass, field
from datetime import datetime, timezone
from typing import List


@dataclass
class Creator:
    id: int
    login: str
    password: str
    firstname: str
    lastname: str


@dataclass
class Article:
    id: int
    creator_id: int
    title: str
    content: str
    created: str = field(default_factory=lambda: datetime.now(timezone.utc).isoformat())
    modified: str = field(default_factory=lambda: datetime.now(timezone.utc).isoformat())
    tag_ids: List[int] = field(default_factory=list)


@dataclass
class Tag:
    id: int
    name: str


@dataclass
class Message:
    id: int
    article_id: int
    content: str
