from dataclasses import dataclass

from app.models.base import BaseEntity
from app.models.user_role import UserRole


@dataclass
class User(BaseEntity):
    login: str = ""
    password: str = ""
    firstname: str = ""
    lastname: str = ""
    role: UserRole = UserRole.CUSTOMER
