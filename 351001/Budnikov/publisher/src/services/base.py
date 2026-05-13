from typing import TypeVar, Generic, Type, List

from tortoise.models import Model
from tortoise.exceptions import IntegrityError
from pydantic import BaseModel, TypeAdapter

from src.core.exceptions import BaseAppException
from src.core.cache import get_cache, set_cache, invalidate_cache_by_prefix

ModelType = TypeVar("ModelType", bound=Model)
CreateSchemaType = TypeVar("CreateSchemaType", bound=BaseModel)
UpdateSchemaType = TypeVar("UpdateSchemaType", bound=BaseModel)
ResponseSchemaType = TypeVar("ResponseSchemaType", bound=BaseModel)


class BaseCRUDService(Generic[ModelType, CreateSchemaType, UpdateSchemaType, ResponseSchemaType]):
    def __init__(self, model: Type[ModelType], response_schema: Type[ResponseSchemaType]):
        self.model = model
        self.response_schema = response_schema
        self.cache_prefix = model.__name__.lower()

    async def get_all(self) -> List[ResponseSchemaType]:
        cache_key = f"{self.cache_prefix}:all"

        cached_data = await get_cache(cache_key)
        if cached_data:
            adapter = TypeAdapter(List[self.response_schema])
            return adapter.validate_json(cached_data)

        objs = await self.model.all()
        results = [self.response_schema.model_validate(obj) for obj in objs]

        adapter = TypeAdapter(List[self.response_schema])
        await set_cache(cache_key, adapter.dump_json(results), ex=3600)
        return results

    async def get_by_id(self, obj_id: int) -> ResponseSchemaType:
        cache_key = f"{self.cache_prefix}:{obj_id}"

        cached_data = await get_cache(cache_key)

        if cached_data:
            return self.response_schema.model_validate_json(cached_data)

        obj = await self.model.get_or_none(id=obj_id)

        if not obj:
            raise BaseAppException(404, "40401", f"{self.model.__name__} with id {obj_id} not found")

        result = self.response_schema.model_validate(obj)
        await set_cache(cache_key, result.model_dump_json(), ex=3600)
        return result

    async def create(self, create_dto: CreateSchemaType) -> ResponseSchemaType:
        try:
            data = create_dto.model_dump(exclude_unset=True)
            obj = await self.model.create(**data)
            await obj.refresh_from_db()
        except IntegrityError as e:
            raise BaseAppException(403, "40301", f"Validation Error: {str(e)}")

        result = self.response_schema.model_validate(obj)

        await invalidate_cache_by_prefix(self.cache_prefix)
        return result

    async def update(self, obj_id: int, update_dto: UpdateSchemaType) -> ResponseSchemaType:
        obj = await self.model.get_or_none(id=obj_id)
        if not obj:
            raise BaseAppException(404, "40402", f"{self.model.__name__} not found")
        try:
            data = update_dto.model_dump(exclude_unset=True)
            await obj.update_from_dict(data).save()
            await obj.refresh_from_db()
        except IntegrityError as e:
            raise BaseAppException(403, "40301", f"Validation Error: {str(e)}")

        result = self.response_schema.model_validate(obj)

        await invalidate_cache_by_prefix(self.cache_prefix)
        return result

    async def delete(self, obj_id: int) -> None:
        deleted_count = await self.model.filter(id=obj_id).delete()

        if not deleted_count:
            raise BaseAppException(404, "40403", f"{self.model.__name__} not found")

        await invalidate_cache_by_prefix(self.cache_prefix)