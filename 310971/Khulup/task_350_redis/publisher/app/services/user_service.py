from app.repositories.user_repository import UserRepository
from app.schemas.user import UserCreate
from app.models.entities import UserEntity
from app.core.cache_decorators import cache_result, invalidate_cache_pattern
from typing import Optional
import logging

logger = logging.getLogger(__name__)

class UserService:
    def __init__(self, repo: UserRepository):
        self.repo = repo

    @invalidate_cache_pattern("user")
    def create_user(self, user_data: UserCreate) -> UserEntity:
        try:
            return self.repo.add(user_data)
        except ValueError as e:
            if "already exists" in str(e):
                raise ValueError("User with this login already exists")
            raise

    @cache_result("user", ttl=3600)
    def get_user(self, user_id: int) -> Optional[UserEntity]:
        return self.repo.get_by_id(user_id)

    @cache_result("user", ttl=1800)
    def list_users(
        self,
        login: str | None = None,
        limit: int = 50,
        offset: int = 0,
        sort_by: str | None = None,
        sort_dir: str = "desc",
    ) -> list[UserEntity]:
        return self.repo.list_users(
            login=login, limit=limit, offset=offset, sort_by=sort_by, sort_dir=sort_dir
        )

    @invalidate_cache_pattern("user")
    def delete_user(self, user_id: int) -> bool:
        return self.repo.delete(user_id)

    @invalidate_cache_pattern("user")
    def update_user(self, user_id: int, user_data: UserCreate) -> Optional[UserEntity]:
        return self.repo.update(user_id, user_data)




