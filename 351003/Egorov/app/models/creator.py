from dataclasses import dataclass
from enum import Enum


class CreatorRole(str, Enum):
    ADMIN = "ADMIN"
    CUSTOMER = "CUSTOMER"


@dataclass
class Creator:
    id: int | None = None
    login: str = ""
    password: str = ""
    first_name: str = ""
    last_name: str = ""
    role: CreatorRole = CreatorRole.CUSTOMER
    name: str = ""
    email: str = ""

