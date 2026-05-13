from typing import List

from Task310.src.core.constants import ErrorStatus
from Task310.src.core.errors import HttpNotFoundError
from Task310.src.core.errors.messages import CreatorErrorMessage
from Task310.src.domain.models import Creator
from Task310.src.domain.repositories.interfaces import Repository
from Task310.src.schemas.creator import CreatorResponseTo, CreatorRequestTo

class CreatorService:
    def __init__(self, repo: Repository[Creator]) -> None:
        self._repo = repo

    def get_one(self, creator_id_str: str) -> CreatorResponseTo:
        try:
            creator_id = int(creator_id_str)
        except ValueError:
            raise HttpNotFoundError(CreatorErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        try:
            creator = self._repo.get_one(creator_id)
        except KeyError:
            raise HttpNotFoundError(CreatorErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return CreatorResponseTo.model_validate(creator)

    def get_all(self) -> List[CreatorResponseTo]:
        creators = self._repo.get_all()
        return [CreatorResponseTo.model_validate(c) for c in creators]

    def create(self, dto: CreatorRequestTo) -> CreatorResponseTo:
        creator = Creator(
            id=0,
            login=dto.login,
            password=dto.password,
            firstname=dto.firstname,
            lastname=dto.lastname
        )
        created = self._repo.create(creator)
        return CreatorResponseTo.model_validate(created)

    def update(self, creator_id_str: str, dto: CreatorRequestTo) -> CreatorResponseTo:
        try:
            creator_id = int(creator_id_str)
        except ValueError:
            raise HttpNotFoundError(CreatorErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        creator = Creator(
            id=creator_id,
            login=dto.login,
            password=dto.password,
            firstname=dto.firstname,
            lastname=dto.lastname
        )
        try:
            updated = self._repo.update(creator)
        except KeyError:
            raise HttpNotFoundError(CreatorErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        return CreatorResponseTo.model_validate(updated)

    def delete(self, creator_id_str: str) -> None:
        try:
            creator_id = int(creator_id_str)
        except ValueError:
            raise HttpNotFoundError(CreatorErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)
        try:
            self._repo.delete(creator_id)
        except KeyError:
            raise HttpNotFoundError(CreatorErrorMessage.NOT_FOUND, ErrorStatus.NOT_FOUND)