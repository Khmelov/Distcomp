from app.models.base import BaseEntity


class User(BaseEntity):
    def __init__(self,
                 id: int,
                 login: str,
                 password: str,
                 firstname: str,
                 lastname: str):
        self.id = id
        self.login = login
        self.password = password
        self.firstname = firstname
        self.lastname = lastname
