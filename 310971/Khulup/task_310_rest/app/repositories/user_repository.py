from app.models.user import User
from app.schemas.user import UserCreate
from app.repositories.base_repository import BaseRepository
from typing import Optional

class UserRepository(BaseRepository[User]):
    def __init__(self):
        self._users = []
        self._id_counter = 1

    def add(self, user_data: UserCreate) -> User:
        user = User(
            id=self._id_counter,
            login=user_data.login,
            password=user_data.password,
            firstname=user_data.firstname,
            lastname=user_data.lastname
        )
        self._id_counter += 1
        self._users.append(user)
        return user

    def get_by_id(self, user_id: int) -> Optional[User]:
        return next((u for u in self._users if u.id == user_id), None)

    def list_users(self) -> list[User]:
        return self._users.copy()

    def list(self) -> list[User]:
        return self._users.copy()

    def delete(self, user_id: int) -> bool:
        for i, user in enumerate(self._users):
            if user.id == user_id:
                del self._users[i]
                return True
        return False

    def update(self, user_id: int, user_data: UserCreate) -> Optional[User]:
        for i, user in enumerate(self._users):
            if user.id == user_id:
                updated_user = User(
                    id=user_id,
                    login=user_data.login,
                    password=user_data.password,
                    firstname=user_data.firstname,
                    lastname=user_data.lastname
                )
                self._users[i] = updated_user
                return updated_user
        return None



