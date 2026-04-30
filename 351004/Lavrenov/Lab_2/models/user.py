from dataclasses import dataclass, field
from .base import BaseEntity


@dataclass
class User(BaseEntity):
    login: str
    password: str
    firstname: str
    lastname: str
    id: int = None
