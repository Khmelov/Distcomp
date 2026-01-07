from app.models.user import User
from app.schemas.user import UserCreate
from app.repositories.base_repository import BaseRepository
from app.models.entities import UserEntity
from app.core.db import SessionLocal
from typing import Optional
from sqlalchemy.orm import Session

class UserRepository(BaseRepository[UserEntity]):
    def __init__(self, session: Optional[Session] = None):
        self.session = session or SessionLocal()

    def add(self, user_data: UserCreate) -> UserEntity:
        existing_user = self.session.query(UserEntity).filter(UserEntity.login == user_data.login).first()
        if existing_user:
            raise ValueError("User with this login already exists")
        
        user_entity = UserEntity(
            login=user_data.login,
            password=user_data.password,
            firstname=user_data.firstname,
            lastname=user_data.lastname
        )
        self.session.add(user_entity)
        self.session.commit()
        self.session.refresh(user_entity)
        return user_entity

    def get_by_id(self, user_id: int) -> Optional[UserEntity]:
        user_entity = self.session.query(UserEntity).filter(UserEntity.id == user_id).first()
        return user_entity

    def list_users(self, login: str | None = None, limit: int = 50, offset: int = 0, 
                   sort_by: str | None = None, sort_dir: str = "desc") -> list[UserEntity]:
        query = self.session.query(UserEntity)
        
        if login:
            query = query.filter(UserEntity.login.contains(login))
        
        if sort_by:
            sort_col = getattr(UserEntity, sort_by, UserEntity.id)
            if sort_dir == "desc":
                query = query.order_by(sort_col.desc())
            else:
                query = query.order_by(sort_col.asc())
        else:
            query = query.order_by(UserEntity.id.desc())
        
        query = query.offset(offset).limit(limit)
        users = query.all()
        return users

    def list(self) -> list[UserEntity]:
        users = self.session.query(UserEntity).all()
        return users

    def delete(self, user_id: int) -> bool:
        user_entity = self.session.query(UserEntity).filter(UserEntity.id == user_id).first()
        if user_entity:
            self.session.delete(user_entity)
            self.session.commit()
            return True
        return False

    def update(self, user_id: int, user_data: UserCreate) -> Optional[UserEntity]:
        user_entity = self.session.query(UserEntity).filter(UserEntity.id == user_id).first()
        if user_entity:
            user_entity.login = user_data.login
            user_entity.password = user_data.password
            user_entity.firstname = user_data.firstname
            user_entity.lastname = user_data.lastname
            self.session.commit()
            self.session.refresh(user_entity)
            return user_entity
        return None



