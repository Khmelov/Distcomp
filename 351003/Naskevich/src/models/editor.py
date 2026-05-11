from dataclasses import dataclass, field

from src.models.user_role import UserRole


@dataclass(kw_only=True)
class Editor:
    id: int = field(default=0, init=False)
    login: str
    password: str
    firstname: str
    lastname: str
    role: UserRole = UserRole.CUSTOMER
