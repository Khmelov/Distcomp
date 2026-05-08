from dataclasses import dataclass, field


@dataclass(kw_only=True)
class Editor:
    id: int = field(default=0, init=False)
    login: str
    password: str
    firstname: str
    lastname: str