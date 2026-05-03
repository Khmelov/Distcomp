from models.user import User
from dto.requests import UserRequestTo
from dto.responses import UserResponseTo
from .base import BaseService


class UserService(BaseService[User, UserRequestTo, UserResponseTo]):
    def _to_entity(self, request: UserRequestTo) -> User:
        return User(
            login=request.login,
            password=request.password,
            firstname=request.firstname,
            lastname=request.lastname,
        )

    def _to_response(self, entity: User) -> UserResponseTo:
        return UserResponseTo(
            id=entity.id,
            login=entity.login,
            firstname=entity.firstname,
            lastname=entity.lastname,
        )
