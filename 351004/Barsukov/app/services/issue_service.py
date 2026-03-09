from sqlalchemy.orm import Session
from repository import IssueRepo
from app.schemas.issue import IssueRequestTo

class IssueService:
    def __init__(self):
        self.repo = IssueRepo()

    def create(self, db: Session, dto: IssueRequestTo):
        data = dto.model_dump(exclude_none=True)
        # Маппинг CamelCase -> snake_case
        if "authorId" in data: data["author_id"] = data.pop("authorId")
        if "stickerIds" in data: data.pop("stickerIds")
        return self.repo.create(db, data)

    def get_all(self, db: Session, skip=0, limit=10):
        return self.repo.get_all(db, skip=skip, limit=limit)

    def get_by_id(self, db: Session, id: int):
        return self.repo.get_by_id(db, id)

    def update(self, db: Session, id: int, dto: IssueRequestTo):
        data = dto.model_dump(exclude_none=True)
        if "authorId" in data: data["author_id"] = data.pop("authorId")
        return self.repo.update(db, id, data)

    def delete(self, db: Session, id: int):
        return self.repo.delete(db, id)
