from dataclasses import dataclass, field
from datetime import datetime, timezone


@dataclass(kw_only=True)
class Tweet:
    id: int = field(default=0, init=False)
    editor_id: int
    title: str
    content: str
    created: datetime = field(default_factory=lambda: datetime.now(timezone.utc))
    modified: datetime = field(default_factory=lambda: datetime.now(timezone.utc))
    markers: list = field(default_factory=list)