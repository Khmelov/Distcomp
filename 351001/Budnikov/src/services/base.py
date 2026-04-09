from typing import TypeVar, Generic, Type, List
from tortoise.models import Model
from pydantic import BaseModel
from src.core.exceptions import BaseAppException


ModelType = TypeVar("ModelType", bound=Model)
CreateSchemaType = TypeVar("CreateSchemaType", bound=BaseModel)
UpdateSchemaType = TypeVar("UpdateSchemaType", bound=BaseModel)
ResponseSchemaType = TypeVar("ResponseSchemaType", bound=BaseModel)


class BaseCRUDService(Generic[ModelType, CreateSchemaType, UpdateSchemaType, ResponseSchemaType]):
    def __init__(self, model: Type[ModelType], response_schema: Type[ResponseSchemaType]):
        self.model = model
        self.response_schema = response_schema

    async def get_all(self) -> List[ResponseSchemaType]:
        objs = await self.model.all()
        return [self.response_schema.model_validate(obj) for obj in objs]

    async def get_by_id(self, obj_id: int) -> ResponseSchemaType:
        obj = await self.model.get_or_none(id=obj_id)
        if not obj:
            raise BaseAppException(404, "40401", f"{self.model.__name__} with id {obj_id} not found")
        return self.response_schema.model_validate(obj)

    async def create(self, create_dto: CreateSchemaType) -> ResponseSchemaType:
        obj = await self.model.create(**create_dto.model_dump())
        return self.response_schema.model_validate(obj)

    async def update(self, obj_id: int, update_dto: UpdateSchemaType) -> ResponseSchemaType:
        obj = await self.model.get_or_none(id=obj_id)
        if not obj:
            raise BaseAppException(404, "40402", f"{self.model.__name__} not found")
        await obj.update_from_dict(update_dto.model_dump(exclude_unset=True)).save()
        return self.response_schema.model_validate(obj)

    async def delete(self, obj_id: int) -> None:
        deleted_count = await self.model.filter(id=obj_id).delete()
        if not deleted_count:
            raise BaseAppException(404, "40403", f"{self.model.__name__} not found")
