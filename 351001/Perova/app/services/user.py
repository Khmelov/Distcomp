from collections.abc import Callable

from app.cache.codec import model_list_to_primitive, model_to_primitive
from app.cache.keys import CacheKeys
from app.dto.user import UserRequestTo, UserResponseTo
from app.exceptions import EntityDuplicateException, EntityNotFoundException
from app.models.user import User
from app.repositories import CrudRepository


class UserService:
    def __init__(
        self,
        repository: CrudRepository[User],
        cache_getter: Callable[[], object | None] | None = None,
    ) -> None:
        self._repository = repository
        self._cache_getter = cache_getter or (lambda: None)

    def get_all(self) -> list[UserResponseTo]:
        key = CacheKeys.user_list()
        cached = self._cache_get(key)
        if isinstance(cached, list):
            return [UserResponseTo.model_validate(item) for item in cached]
        result = [self._to_response(user) for user in self._repository.find_all()]
        self._cache_set(key, model_list_to_primitive(result))
        return result

    def get_by_id(self, user_id: int) -> UserResponseTo:
        key = CacheKeys.user_id(user_id)
        cached = self._cache_get(key)
        if isinstance(cached, dict):
            return UserResponseTo.model_validate(cached)
        user = self._repository.find_by_id(user_id)
        if user is None:
            raise EntityNotFoundException("User", user_id)
        response = self._to_response(user)
        self._cache_set(key, model_to_primitive(response))
        return response

    def create(self, request: UserRequestTo) -> UserResponseTo:
        self._ensure_unique_login(request.login)
        user = User(
            login=request.login,
            password=request.password,
            firstname=request.firstName,
            lastname=request.lastName,
        )
        created = self._repository.create(user)
        response = self._to_response(created)
        self._invalidate_user_cache(response.id)
        return response

    def update(self, request: UserRequestTo) -> UserResponseTo:
        if request.id is None:
            raise EntityNotFoundException("User", 0)
        existing = self._repository.find_by_id(request.id)
        if existing is None:
            raise EntityNotFoundException("User", request.id)
        self._ensure_unique_login(request.login, ignore_user_id=request.id)
        existing.login = request.login
        existing.password = request.password
        existing.firstname = request.firstName
        existing.lastname = request.lastName
        updated = self._repository.update(existing)
        response = self._to_response(updated)
        self._invalidate_user_cache(response.id)
        return response

    def delete(self, user_id: int) -> None:
        if not self._repository.delete_by_id(user_id):
            raise EntityNotFoundException("User", user_id)
        self._invalidate_user_cache(user_id)

    def find_by_login(self, login: str) -> User | None:
        return next((user for user in self._repository.find_all() if user.login == login), None)

    def _ensure_unique_login(self, login: str, ignore_user_id: int | None = None) -> None:
        for user in self._repository.find_all():
            if user.login == login and user.id != ignore_user_id:
                raise EntityDuplicateException("login", login)

    @staticmethod
    def _to_response(user: User) -> UserResponseTo:
        return UserResponseTo(
            id=user.id,
            login=user.login,
            password=user.password,
            firstname=user.firstname,
            lastname=user.lastname,
        )

    def _invalidate_user_cache(self, user_id: int | None = None) -> None:
        self._cache_delete(CacheKeys.user_list())
        if user_id is not None:
            self._cache_delete(CacheKeys.user_id(user_id))
        self._cache_delete_pattern(f"{CacheKeys.PREFIX}:issue:id:*:user")
        self._cache_delete_pattern(f"{CacheKeys.PREFIX}:issue:search:*")

    def _cache_get(self, key: str):
        cache = self._cache_getter()
        if cache is None:
            return None
        return cache.get_json(key)

    def _cache_set(self, key: str, value: object) -> None:
        cache = self._cache_getter()
        if cache is None:
            return
        cache.set_json(key, value)

    def _cache_delete(self, key: str) -> None:
        cache = self._cache_getter()
        if cache is None:
            return
        cache.delete(key)

    def _cache_delete_pattern(self, pattern: str) -> None:
        cache = self._cache_getter()
        if cache is None:
            return
        cache.delete_by_pattern(pattern)
