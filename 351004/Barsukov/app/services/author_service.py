from sqlalchemy.orm import Session
from repository import AuthorRepo
from app.schemas.author import AuthorRequestTo

class AuthorService:
    def __init__(self):
        self.repo = AuthorRepo()

    def create(self, db: Session, dto: AuthorRequestTo):
        return self.repo.create(db, dto.model_dump(exclude_none=True))

    def get_all(self, db: Session, skip=0, limit=10, sort="id", name=None):
        return self.repo.get_all(db, skip=skip, limit=limit, sort_by=sort, firstname=name)

    def get_by_id(self, db: Session, id: int):
        return self.repo.get_by_id(db, id)

    def update(self, db: Session, id: int, dto: AuthorRequestTo):
        return self.repo.update(db, id, dto.model_dump(exclude_none=True))

    def delete(self, db: Session, id: int):
        return self.repo.delete(db, id)
