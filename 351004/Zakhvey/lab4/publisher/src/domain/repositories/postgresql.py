from typing import Generic, TypeVar, List, Type

from sqlalchemy import select
from sqlalchemy.exc import IntegrityError
from sqlalchemy.ext.asyncio import AsyncSession

from src.core.errors.errors import ResourceAlreadyExistsError, ResourceNotFoundError
from src.domain.models import Base
from src.domain.repositories.base import AsyncRepository

ModelType = TypeVar("ModelType", bound=Base)

class SqlAlchemyRepository(AsyncRepository, Generic[ModelType]):
    def __init__(self, session: AsyncSession, model_class: Type[ModelType]):
        self.session = session
        self.model_class = model_class

    async def get_one(self, entity_id: int) -> ModelType:
        result = await self.session.execute(
            select(self.model_class).where(self.model_class.id == entity_id))

        entity = result.scalar_one_or_none()

        if not entity:
            raise ResourceNotFoundError(f"{self.model_class} with id {entity_id} not found")

        return entity

    async def get_all(self) -> List[ModelType]:
        result = await self.session.execute(
            select(self.model_class)
        )
        return list(result.scalars().all())

    async def create(self, **kwargs) -> ModelType:
        try:
            entity = self.model_class(**kwargs)
            self.session.add(entity)
            await self.session.flush()
            await self.session.refresh(entity)
        except IntegrityError as e:
            await self.session.rollback()
            raise ResourceAlreadyExistsError(f"{self.model_class.__name__} already exists") from e


        return entity

    async def update(self, entity_id: int, **kwargs) -> ModelType:
        entity = await self.get_one(entity_id)
        if not entity:
            raise ResourceNotFoundError(f"{self.model_class} with id {entity_id} not found")

        for key, value in kwargs.items():
            if hasattr(self.model_class, key):
                setattr(entity, key, value)

        try:
            await self.session.flush()
            await self.session.refresh(entity)
        except IntegrityError as e:
            await self.session.rollback()
            raise ResourceAlreadyExistsError(f"{self.model_class.__name__} already exists") from e

        return entity

    async def delete(self, entity_id: int) -> None:
        entity = await self.get_one(entity_id)
        if not entity:
            raise ResourceNotFoundError(f"{self.model_class} with id {entity_id} not found")
        await self.session.delete(entity)
        await self.session.flush()