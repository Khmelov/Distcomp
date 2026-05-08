from typing import List

from app.dtos.creator_request import CreatorRequestTo
from app.dtos.creator_response import CreatorResponseTo
from app.models.creator import Creator
from app.repositories.base_repository import BaseRepository


class CreatorService:
    def __init__(self, repository: BaseRepository[Creator]) -> None:
        self._repository = repository

    def create_creator(self, dto: CreatorRequestTo) -> CreatorResponseTo:
        entity = Creator(login=dto.login, name=dto.name, email=dto.email)
        created = self._repository.create(entity)
        return self._to_response(created)

    def get_creator(self, creator_id: int) -> CreatorResponseTo | None:
        entity = self._repository.read_by_id(creator_id)
        return self._to_response(entity) if entity else None

    def get_all_creators(self) -> List[CreatorResponseTo]:
        return [self._to_response(c) for c in self._repository.read_all()]

    def update_creator(self, creator_id: int, dto: CreatorRequestTo) -> CreatorResponseTo | None:
        if self._repository.read_by_id(creator_id) is None:
            return None
        entity = Creator(id=creator_id, login=dto.login, name=dto.name, email=dto.email)
        updated = self._repository.update(creator_id, entity)
        return self._to_response(updated)

    def delete_creator(self, creator_id: int) -> bool:
        if self._repository.read_by_id(creator_id) is None:
            return False
        self._repository.delete(creator_id)
        return True

    @staticmethod
    def _to_response(entity: Creator) -> CreatorResponseTo:
        return CreatorResponseTo(
            id=entity.id or 0,
            login=entity.login,
            name=entity.name,
            email=entity.email,
            links={
                "self": f"/api/v1.0/creators/{entity.id}",
            },
        )

