from dataclasses import dataclass


@dataclass
class Author:
    id: int
    login: str
    password: str
    firstname: str
    lastname: str