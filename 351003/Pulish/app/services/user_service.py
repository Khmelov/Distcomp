from app.dto.user import UserRequestTo, UserResponseTo
from app.models.user import User
from app.repository.inmemory import InMemoryRepository
from app.core.exceptions import NotFoundException


class UserService:
    def __init__(self):
        self.repo = InMemoryRepository()

    def create(self, dto: UserRequestTo) -> UserResponseTo:
        user = User(
            id=0,
            login=dto.login,
            password=dto.password,
            firstname=dto.firstname,
            lastname=dto.lastname
        )
        saved = self.repo.create(user)
        return self._to_response(saved)

    def find_all(self):
        return [self._to_response(u) for u in self.repo.find_all()]

    def find_by_id(self, id: int):
        user = self.repo.find_by_id(id)
        if not user:
            raise NotFoundException("User not found", 40401)
        return self._to_response(user)

    def update(self, id: int, dto: UserRequestTo):
        user = self.repo.find_by_id(id)
        if not user:
            raise NotFoundException("User not found", 40401)
        user.login = dto.login
        user.firstname = dto.firstname
        user.lastname = dto.lastname
        return self._to_response(user)

    def delete(self, id: int):
        user = self.repo.find_by_id(id)
        if not user:
            raise NotFoundException("User not found", 40401)
        self.repo.delete(id)

    def _to_response(self, user: User) -> UserResponseTo:
        return UserResponseTo(
            id=user.id,
            login=user.login,
            firstname=user.firstname,
            lastname=user.lastname
        )
