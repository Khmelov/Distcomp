from app.repository.memory import sticker_repo
from app.schemas.sticker import StickerRequestTo, StickerResponseTo
from app.core.exceptions import AppException


class StickerService:
    def create(self, dto: StickerRequestTo) -> StickerResponseTo:
        return StickerResponseTo(**sticker_repo.save(dto.model_dump()))

    def get_all(self):
        return [StickerResponseTo(**s) for s in sticker_repo.find_all()]

    def get_by_id(self, id: int):
        res = sticker_repo.find_by_id(id)
        if not res:
            raise AppException(404, "Sticker not found", 9)
        return StickerResponseTo(**res)

    def update(self, id: int, dto: StickerRequestTo) -> StickerResponseTo:
        if not sticker_repo.find_by_id(id):
            raise AppException(404, "Sticker not found", 10)
        data = dto.model_dump()
        data["id"] = id
        saved = sticker_repo.save(data)
        return StickerResponseTo(**saved)

    def delete(self, id: int):
        if not sticker_repo.delete(id):
            raise AppException(404, "Sticker not found", 11)
