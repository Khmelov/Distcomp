from app.repositories.user_repository import UserRepository
from app.schemas.user import UserCreate
from app.models.user import User
from typing import Optional

class UserService:
    def __init__(self, repo: UserRepository):
        self.repo = repo

    def create_user(self, user_data: UserCreate) -> User:
        return self.repo.add(user_data)

    def get_user(self, user_id: int) -> Optional[User]:
        return self.repo.get_by_id(user_id)

    def list_users(self) -> list[User]:
        return self.repo.list_users()

    def delete_user(self, user_id: int) -> bool:
        return self.repo.delete(user_id)

    def update_user(self, user_id: int, user_data: UserCreate) -> Optional[User]:
        return self.repo.update(user_id, user_data)




