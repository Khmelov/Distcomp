from typing import List, Optional

from Task350.publisher.src.core.constants import ErrorStatus
from Task350.publisher.src.core.errors import HttpNotFoundError, HttpBadRequestError
from Task350.publisher.src.core.errors.messages import MarkerErrorMessage
from Task350.publisher.src.domain.models import Marker
from Task350.publisher.src.domain.repositories.interfaces import Repository
from Task350.publisher.src.schemas.marker import MarkerResponseTo, MarkerRequestTo
from Task350.publisher.src.infrastructure.redis_client import RedisClient

class MarkerService:
    def __init__(self, repo: Repository[Marker], redis: RedisClient):
        self._repo = repo
        self._redis = redis

    async def get_one(self, marker_id_str: str) -> MarkerResponseTo:
        try:
            marker_id = int(marker_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid marker ID format", ErrorStatus.BAD_REQUEST)

        # Пытаемся получить из Redis
        cache_key = f"marker:{marker_id}"
        cached = await self._redis.get(cache_key)
        if cached:
            return MarkerResponseTo.model_validate(cached)

        # Если нет – из БД
        try:
            marker = await self._repo.get_one(marker_id)
        except KeyError:
            raise HttpNotFoundError(MarkerErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)

        response = MarkerResponseTo.model_validate(marker)
        # Сохраняем в Redis на 60 секунд
        await self._redis.set(cache_key, response.model_dump(), ttl=60)
        return response

    async def get_all(
        self,
        page: int = 1,
        size: int = 20,
        sort: str = "id",
        name: Optional[str] = None
    ) -> List[MarkerResponseTo]:
        # Для списков кеширование не применяем, так как параметров много
        markers = await self._repo.get_all(page=page, size=size, sort=sort, name=name)
        return [MarkerResponseTo.model_validate(m) for m in markers]

    async def create(self, dto: MarkerRequestTo) -> MarkerResponseTo:
        marker = Marker(id=0, name=dto.name)
        created = await self._repo.create(marker)
        response = MarkerResponseTo.model_validate(created)
        # При создании не удаляем кеш (новый маркер ещё не в кеше)
        return response

    async def update(self, marker_id_str: str, dto: MarkerRequestTo) -> MarkerResponseTo:
        try:
            marker_id = int(marker_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid marker ID format", ErrorStatus.BAD_REQUEST)

        marker = Marker(id=marker_id, name=dto.name)
        try:
            updated = await self._repo.update(marker)
        except KeyError:
            raise HttpNotFoundError(MarkerErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)

        # Инвалидируем кеш
        await self._redis.delete(f"marker:{marker_id}")
        return MarkerResponseTo.model_validate(updated)

    async def delete(self, marker_id_str: str) -> None:
        try:
            marker_id = int(marker_id_str)
        except ValueError:
            raise HttpBadRequestError("Invalid marker ID format", ErrorStatus.BAD_REQUEST)

        try:
            await self._repo.delete(marker_id)
        except KeyError:
            raise HttpNotFoundError(MarkerErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)

        # Инвалидируем кеш
        await self._redis.delete(f"marker:{marker_id}")

    async def get_markers_by_tweet_id(self, tweet_id: int) -> List[MarkerResponseTo]:
        # Можно тоже закешировать, но для простоты оставим без кеша
        markers = await self._repo.get_by_tweet_id(tweet_id)
        return [MarkerResponseTo.model_validate(m) for m in markers]

    # Метод для PUT с ID в теле (если используется)
    async def update_from_body(self, dto: MarkerRequestTo) -> MarkerResponseTo:
        if dto.id is None:
            raise HttpBadRequestError("Missing marker ID", ErrorStatus.BAD_REQUEST)
        try:
            await self._repo.get_one(dto.id)  # проверка существования
        except KeyError:
            raise HttpNotFoundError(MarkerErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        marker = Marker(id=dto.id, name=dto.name)
        updated = await self._repo.update(marker)
        # Инвалидируем кеш
        await self._redis.delete(f"marker:{dto.id}")
        return MarkerResponseTo.model_validate(updated)