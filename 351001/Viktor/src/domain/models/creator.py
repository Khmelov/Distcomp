from dataclasses import dataclass

@dataclass
class Creator:
    id: int
    login: str
    password: str
    firstname: str
    lastname: str