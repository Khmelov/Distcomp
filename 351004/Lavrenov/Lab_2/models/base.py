from abc import ABC
from datetime import datetime
from typing import Optional


class BaseEntity(ABC):
    id: Optional[int] = None
    created: Optional[datetime] = None
    modified: Optional[datetime] = None
