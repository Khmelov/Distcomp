from src.cache import keys as cache_keys
from src.cache.redis_cache import RedisCache
from src.database.uow import UnitOfWork
from src.dto.marker import MarkerRequestTo, MarkerResponseTo
from src.exceptions import EntityAlreadyExistsException, EntityNotFoundException
from src.models.marker import Marker
from src.repositories.marker import AbstractMarkerRepository


class MarkerService:

    def __init__(
        self,
        repository: AbstractMarkerRepository,
        uow: UnitOfWork,
        cache: RedisCache,
        cache_ttl_seconds: int,
    ) -> None:
        self._repo = repository
        self._uow = uow
        self._cache = cache
        self._ttl = cache_ttl_seconds

    async def _invalidate_markers(self, marker_id: int | None = None) -> None:
        keys = [cache_keys.markers_all()]
        if marker_id is not None:
            keys.append(cache_keys.marker(marker_id))
        await self._cache.delete(*keys)

    async def get_by_id(self, marker_id: int) -> MarkerResponseTo:
        ck = cache_keys.marker(marker_id)
        cached = await self._cache.get_json(ck)
        if cached is not None:
            return MarkerResponseTo.model_validate(cached)
        marker = await self._repo.get_by_id(marker_id)
        if marker is None:
            raise EntityNotFoundException("Marker", marker_id)
        dto = MarkerResponseTo.model_validate(marker)
        await self._cache.set_json(ck, dto.model_dump(mode="json"), ttl_seconds=self._ttl)
        return dto

    async def get_all(self) -> list[MarkerResponseTo]:
        ck = cache_keys.markers_all()
        cached = await self._cache.get_json(ck)
        if cached is not None:
            return [MarkerResponseTo.model_validate(x) for x in cached]
        markers = await self._repo.get_all()
        out = [MarkerResponseTo.model_validate(m) for m in markers]
        await self._cache.set_json(
            ck,
            [m.model_dump(mode="json") for m in out],
            ttl_seconds=self._ttl,
        )
        return out

    async def create(self, data: MarkerRequestTo) -> MarkerResponseTo:
        existing = await self._repo.get_by_name(data.name)
        if existing is not None:
            raise EntityAlreadyExistsException("Marker", "name", data.name)
        marker = Marker(name=data.name)
        created = await self._repo.create(marker)
        await self._uow.commit()
        dto = MarkerResponseTo.model_validate(created)
        await self._invalidate_markers()
        await self._cache.set_json(
            cache_keys.marker(dto.id),
            dto.model_dump(mode="json"),
            ttl_seconds=self._ttl,
        )
        return dto

    async def update(self, marker_id: int, data: MarkerRequestTo) -> MarkerResponseTo:
        existing = await self._repo.get_by_name(data.name)
        if existing is not None and existing.id != marker_id:
            raise EntityAlreadyExistsException("Marker", "name", data.name)
        marker = Marker(name=data.name)
        marker.id = marker_id
        updated = await self._repo.update(marker)
        if updated is None:
            raise EntityNotFoundException("Marker", marker_id)
        await self._uow.commit()
        dto = MarkerResponseTo.model_validate(updated)
        await self._invalidate_markers(marker_id)
        await self._cache.set_json(
            cache_keys.marker(marker_id),
            dto.model_dump(mode="json"),
            ttl_seconds=self._ttl,
        )
        return dto

    async def delete(self, marker_id: int) -> None:
        deleted = await self._repo.delete(marker_id)
        if not deleted:
            raise EntityNotFoundException("Marker", marker_id)
        await self._uow.commit()
        await self._invalidate_markers(marker_id)
