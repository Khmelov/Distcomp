from app.dto.user import UserRequestTo, UserResponseTo
from app.exceptions import EntityDuplicateException, EntityNotFoundException
from app.models.user import User
from app.repositories import CrudRepository


class UserService:
    def __init__(self, repository: CrudRepository[User]) -> None:
        self._repository = repository

    def get_all(self) -> list[UserResponseTo]:
        return [self._to_response(user) for user in self._repository.find_all()]

    def get_by_id(self, user_id: int) -> UserResponseTo:
        user = self._repository.find_by_id(user_id)
        if user is None:
            raise EntityNotFoundException("User", user_id)
        return self._to_response(user)

    def create(self, request: UserRequestTo) -> UserResponseTo:
        self._ensure_unique_login(request.login)
        user = User(
            login=request.login,
            password=request.password,
            firstname=request.firstname,
            lastname=request.lastname,
        )
        created = self._repository.create(user)
        return self._to_response(created)

    def update(self, request: UserRequestTo) -> UserResponseTo:
        if request.id is None:
            raise EntityNotFoundException("User", 0)
        existing = self._repository.find_by_id(request.id)
        if existing is None:
            raise EntityNotFoundException("User", request.id)
        self._ensure_unique_login(request.login, ignore_user_id=request.id)
        existing.login = request.login
        existing.password = request.password
        existing.firstname = request.firstname
        existing.lastname = request.lastname
        updated = self._repository.update(existing)
        return self._to_response(updated)

    def delete(self, user_id: int) -> None:
        if not self._repository.delete_by_id(user_id):
            raise EntityNotFoundException("User", user_id)

    def find_by_login(self, login: str) -> User | None:
        return next((user for user in self._repository.find_all() if user.login == login), None)

    def _ensure_unique_login(self, login: str, ignore_user_id: int | None = None) -> None:
        for user in self._repository.find_all():
            if user.login == login and user.id != ignore_user_id:
                raise EntityDuplicateException("login", login)

    @staticmethod
    def _to_response(user: User) -> UserResponseTo:
        return UserResponseTo.model_validate(user.__dict__)
