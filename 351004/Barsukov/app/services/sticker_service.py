from sqlalchemy.orm import Session
from repository import StickerRepo
from app.schemas.sticker import StickerRequestTo

class StickerService:
    def __init__(self):
        self.repo = StickerRepo()

    def create(self, db: Session, dto: StickerRequestTo):
        return self.repo.create(db, dto.model_dump(exclude_none=True))

    def get_all(self, db: Session):
        return self.repo.get_all(db)

    def get_by_id(self, db: Session, id: int):
        return self.repo.get_by_id(db, id)

    def update(self, db: Session, id: int, dto: StickerRequestTo):
        return self.repo.update(db, id, dto.model_dump(exclude_none=True))

    def delete(self, db: Session, id: int):
        return self.repo.delete(db, id)
