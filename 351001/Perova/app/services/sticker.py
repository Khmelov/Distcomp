from collections.abc import Callable

from app.cache.codec import model_list_to_primitive, model_to_primitive
from app.cache.keys import CacheKeys
from app.dto.sticker import StickerRequestTo, StickerResponseTo
from app.exceptions import EntityNotFoundException
from app.models.sticker import Sticker
from app.repositories import CrudRepository


class StickerService:
    def __init__(
        self,
        repository: CrudRepository[Sticker],
        cache_getter: Callable[[], object | None] | None = None,
    ) -> None:
        self._repository = repository
        self._cache_getter = cache_getter or (lambda: None)

    def get_all(self) -> list[StickerResponseTo]:
        key = CacheKeys.sticker_list()
        cached = self._cache_get(key)
        if isinstance(cached, list):
            return [StickerResponseTo.model_validate(item) for item in cached]
        result = [self._to_response(sticker) for sticker in self._repository.find_all()]
        self._cache_set(key, model_list_to_primitive(result))
        return result

    def get_by_id(self, sticker_id: int) -> StickerResponseTo:
        key = CacheKeys.sticker_id(sticker_id)
        cached = self._cache_get(key)
        if isinstance(cached, dict):
            return StickerResponseTo.model_validate(cached)
        sticker = self._repository.find_by_id(sticker_id)
        if sticker is None:
            raise EntityNotFoundException("Sticker", sticker_id)
        response = self._to_response(sticker)
        self._cache_set(key, model_to_primitive(response))
        return response

    def get_entity_by_id(self, sticker_id: int) -> Sticker:
        sticker = self._repository.find_by_id(sticker_id)
        if sticker is None:
            raise EntityNotFoundException("Sticker", sticker_id)
        return sticker

    def create(self, request: StickerRequestTo) -> StickerResponseTo:
        created = self._repository.create(Sticker(name=request.name))
        response = self._to_response(created)
        self._invalidate_sticker_cache(response.id)
        return response

    def update(self, request: StickerRequestTo) -> StickerResponseTo:
        if request.id is None:
            raise EntityNotFoundException("Sticker", 0)
        existing = self._repository.find_by_id(request.id)
        if existing is None:
            raise EntityNotFoundException("Sticker", request.id)
        existing.name = request.name
        updated = self._repository.update(existing)
        response = self._to_response(updated)
        self._invalidate_sticker_cache(response.id)
        return response

    def delete(self, sticker_id: int) -> None:
        if not self._repository.delete_by_id(sticker_id):
            raise EntityNotFoundException("Sticker", sticker_id)
        self._invalidate_sticker_cache(sticker_id)

    @staticmethod
    def _to_response(sticker: Sticker) -> StickerResponseTo:
        return StickerResponseTo.model_validate(sticker.__dict__)

    def _invalidate_sticker_cache(self, sticker_id: int | None = None) -> None:
        self._cache_delete(CacheKeys.sticker_list())
        if sticker_id is not None:
            self._cache_delete(CacheKeys.sticker_id(sticker_id))
        self._cache_delete_pattern(f"{CacheKeys.PREFIX}:issue:id:*:stickers")
        self._cache_delete_pattern(f"{CacheKeys.PREFIX}:issue:search:*")

    def _cache_get(self, key: str):
        cache = self._cache_getter()
        if cache is None:
            return None
        return cache.get_json(key)

    def _cache_set(self, key: str, value: object) -> None:
        cache = self._cache_getter()
        if cache is None:
            return
        cache.set_json(key, value)

    def _cache_delete(self, key: str) -> None:
        cache = self._cache_getter()
        if cache is None:
            return
        cache.delete(key)

    def _cache_delete_pattern(self, pattern: str) -> None:
        cache = self._cache_getter()
        if cache is None:
            return
        cache.delete_by_pattern(pattern)
