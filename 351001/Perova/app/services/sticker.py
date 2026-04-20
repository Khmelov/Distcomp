from app.dto.sticker import StickerRequestTo, StickerResponseTo
from app.exceptions import EntityNotFoundException
from app.models.sticker import Sticker
from app.repositories import CrudRepository


class StickerService:
    def __init__(self, repository: CrudRepository[Sticker]) -> None:
        self._repository = repository

    def get_all(self) -> list[StickerResponseTo]:
        return [self._to_response(sticker) for sticker in self._repository.find_all()]

    def get_by_id(self, sticker_id: int) -> StickerResponseTo:
        sticker = self._repository.find_by_id(sticker_id)
        if sticker is None:
            raise EntityNotFoundException("Sticker", sticker_id)
        return self._to_response(sticker)

    def get_entity_by_id(self, sticker_id: int) -> Sticker:
        sticker = self._repository.find_by_id(sticker_id)
        if sticker is None:
            raise EntityNotFoundException("Sticker", sticker_id)
        return sticker

    def create(self, request: StickerRequestTo) -> StickerResponseTo:
        created = self._repository.create(Sticker(name=request.name))
        return self._to_response(created)

    def update(self, request: StickerRequestTo) -> StickerResponseTo:
        if request.id is None:
            raise EntityNotFoundException("Sticker", 0)
        existing = self._repository.find_by_id(request.id)
        if existing is None:
            raise EntityNotFoundException("Sticker", request.id)
        existing.name = request.name
        updated = self._repository.update(existing)
        return self._to_response(updated)

    def delete(self, sticker_id: int) -> None:
        if not self._repository.delete_by_id(sticker_id):
            raise EntityNotFoundException("Sticker", sticker_id)

    @staticmethod
    def _to_response(sticker: Sticker) -> StickerResponseTo:
        return StickerResponseTo.model_validate(sticker.__dict__)
