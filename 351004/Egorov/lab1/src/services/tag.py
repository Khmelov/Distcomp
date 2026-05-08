from typing import List

from src.core.constants import ErrorStatus
from src.core.errors import HttpNotFoundError
from src.core.errors.messages import TagErrorMessage
from src.domain.models import Tag
from src.domain.repositories.interfaces import Repository
from src.schemas.tag import TagResponseTo, TagRequestTo


class TagService:
    def __init__(self, repo: Repository[Tag]) -> None:
        self._repo = repo

    def get_one(self, tag_id: int) -> TagResponseTo:
        try:
            tag = self._repo.get_one(tag_id)
        except KeyError:
            raise HttpNotFoundError(TagErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return TagResponseTo.model_validate(tag)

    def get_all(self) -> List[TagResponseTo]:
        tags = self._repo.get_all()
        return [TagResponseTo.model_validate(t) for t in tags]

    def create(self, dto: TagRequestTo) -> TagResponseTo:
        tag = Tag(
            id=0,
            name=dto.name,
        )
        created_tag = self._repo.create(tag)
        return TagResponseTo.model_validate(created_tag)

    def update(self, tag_id: int, dto: TagRequestTo) -> TagResponseTo:
        tag = Tag(
            id=tag_id,
            name=dto.name,
        )
        try:
            updated_tag = self._repo.update(tag)
        except KeyError:
            raise HttpNotFoundError(TagErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return TagResponseTo.model_validate(updated_tag)

    def delete(self, tag_id: int) -> None:
        try:
            self._repo.delete(tag_id)
        except KeyError:
            raise HttpNotFoundError(TagErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
