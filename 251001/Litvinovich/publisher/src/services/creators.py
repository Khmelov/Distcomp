from fastapi import HTTPException

from src.models.models import Creator
from src.schemas.creators import CreatorRequestToAdd, CreatorResponseTo, CreatorRequestToUpdate
from src.utils.repository import AbstractRepository, NotFoundError


class CreatorsService:
    def __init__(self, creators_repo: AbstractRepository):
        self.creators_repo: AbstractRepository = creators_repo

    async def create_creator(self, creator: CreatorRequestToAdd) -> CreatorResponseTo:
        creator_model = Creator(
            login=creator.login,
            password=creator.password,
            firstname=creator.firstname,
            lastname=creator.lastname
        )

        try:
            created_model = await self.creators_repo.create(creator_model)
            return CreatorResponseTo.model_validate(created_model)
        except ValueError as e:
            raise HTTPException(status_code=403, detail=str(e))

    async def get_creators(self) -> list[CreatorResponseTo]:
        creators = await self.creators_repo.get_all()
        return [CreatorResponseTo.model_validate(creator) for creator in creators]

    async def get_creator_by_id(self, creator_id: int) -> CreatorResponseTo:
        try:
            creator = await self.creators_repo.get_by_id(creator_id)
            return CreatorResponseTo.model_validate(creator)
        except NotFoundError:
            raise HTTPException(status_code=404, detail="Creator not found")

    async def delete_creator(self, creator_id: int):
        try:
            await self.creators_repo.delete(creator_id)
        except NotFoundError:
            raise HTTPException(status_code=404, detail="Creator not found")

    async def update_creator(self, creator: CreatorRequestToUpdate) -> CreatorResponseTo:
        try:
            existing_creator = await self.creators_repo.get_by_id(creator.id)

            existing_creator.login = creator.login
            existing_creator.password = creator.password
            existing_creator.firstname = creator.firstname
            existing_creator.lastname = creator.lastname

            updated_creator = await self.creators_repo.update(existing_creator)
            return CreatorResponseTo.model_validate(updated_creator)
        except NotFoundError:
            raise HTTPException(status_code=404, detail="Creator not found")