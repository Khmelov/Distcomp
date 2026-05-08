from sqlalchemy.orm import Session
from sqlalchemy.exc import IntegrityError
from app.dto.user import UserRequestTo, UserResponseTo
from app.models.user import User
from app.models.mark import Mark
from app.core.exceptions import NotFoundException, AppException
from app.cache.redis_client import cache_get, cache_set, cache_delete
from app.core.security import hash_password


class UserService:
    def __init__(self, db: Session):
        self.db = db

    def create(self, dto: UserRequestTo) -> UserResponseTo:
        user = User(
            login=dto.login,
            password=hash_password(dto.password),
            firstname=dto.firstname,
            lastname=dto.lastname,
            role=dto.role,
        )
        try:
            self.db.add(user)
            self.db.commit()
            self.db.refresh(user)
        except IntegrityError:
            self.db.rollback()
            raise AppException("Login already exists", 40301, 403)
        result = self._to_response(user)
        cache_set(f"user:{user.id}", result.model_dump())
        cache_delete("users:all")
        return result

    def find_all(self):
        cached = cache_get("users:all")
        if cached is not None:
            return [UserResponseTo(**u) for u in cached]
        users = self.db.query(User).all()
        result = [self._to_response(u) for u in users]
        cache_set("users:all", [r.model_dump() for r in result])
        return result

    def find_by_id(self, id: int):
        cached = cache_get(f"user:{id}")
        if cached is not None:
            return UserResponseTo(**cached)
        user = self.db.query(User).filter(User.id == id).first()
        if not user:
            raise NotFoundException("User not found", 40401)
        result = self._to_response(user)
        cache_set(f"user:{id}", result.model_dump())
        return result

    def update(self, dto: UserRequestTo):
        user = self.db.query(User).filter(User.id == dto.id).first()
        if not user:
            raise NotFoundException("User not found", 40401)

        user.login = dto.login
        user.firstname = dto.firstname
        user.lastname = dto.lastname
        user.role = dto.role
        try:
            self.db.commit()
            self.db.refresh(user)
        except IntegrityError:
            self.db.rollback()
            raise AppException("Login already exists", 40301, 403)
        result = self._to_response(user)
        cache_set(f"user:{user.id}", result.model_dump())
        cache_delete("users:all")
        return result

    def delete(self, id: int):
        user = self.db.query(User).filter(User.id == id).first()
        if not user:
            raise NotFoundException("User not found", 40401)

        marks_to_check = set()
        for t in user.topics:
            for m in t.marks:
                marks_to_check.add(m.id)

        self.db.delete(user)
        self.db.commit()

        for m_id in marks_to_check:
            mark_in_db = self.db.query(Mark).filter(Mark.id == m_id).first()
            if mark_in_db and len(mark_in_db.topics) == 0:
                self.db.delete(mark_in_db)
        self.db.commit()

        cache_delete(f"user:{id}")
        cache_delete("users:all")

    def _to_response(self, user: User) -> UserResponseTo:
        return UserResponseTo(
            id=user.id,
            login=user.login,
            firstname=user.firstname,
            lastname=user.lastname,
            role=user.role,
        )
