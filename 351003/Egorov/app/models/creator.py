from dataclasses import dataclass


@dataclass
class Creator:
    id: int | None = None
    login: str = ""
    name: str = ""
    email: str = ""

