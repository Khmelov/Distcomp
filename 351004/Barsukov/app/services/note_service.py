from sqlalchemy.orm import Session
from repository import NoteRepo
from app.schemas.note import NoteRequestTo

class NoteService:
    def __init__(self):
        self.repo = NoteRepo()

    def create(self, db: Session, dto: NoteRequestTo):
        data = dto.model_dump(exclude_none=True)
        if "issueId" in data: data["issue_id"] = data.pop("issueId")
        return self.repo.create(db, data)

    def get_all(self, db: Session):
        return self.repo.get_all(db)

    def get_by_id(self, db: Session, id: int):
        return self.repo.get_by_id(db, id)

    def update(self, db: Session, id: int, dto: NoteRequestTo):
        data = dto.model_dump(exclude_none=True)
        if "issueId" in data: data["issue_id"] = data.pop("issueId")
        return self.repo.update(db, id, data)

    def delete(self, db: Session, id: int):
        return self.repo.delete(db, id)
