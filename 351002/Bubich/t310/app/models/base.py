from dataclasses import dataclass, field
from typing import Optional

@dataclass
class BaseEntity:
    id: Optional[int] = field(default=None)