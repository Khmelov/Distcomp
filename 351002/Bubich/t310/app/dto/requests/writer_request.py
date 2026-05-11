from dataclasses import dataclass

@dataclass
class WriterRequestTo:
    login: str = ""
    password: str = ""
    firstname: str = ""
    lastname: str = ""