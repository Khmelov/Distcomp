from typing import Generic, TypeVar, Dict, List

from motor.motor_asyncio import AsyncIOMotorCollection
from pymongo import ReturnDocument
from pymongo.errors import DuplicateKeyError

from src.core.errors import ResourceNotFoundError, ResourceAlreadyExistsError

DictType = TypeVar("DictType", bound=Dict)

class MongoRepository(Generic[DictType]):
    def __init__(self, collection: AsyncIOMotorCollection):
        self.collection = collection
        self.collection_name = collection.name
        self.counters = collection.database["counters"]

    async def _get_next_id(self) -> int:
        result = await self.counters.find_one_and_update(
            {"_id": self.collection_name},
            {"$inc": {"seq": 1}},
            upsert=True,
            return_document=ReturnDocument.AFTER
        )
        return result["seq"]

    async def get_one(self, entity_id: int) -> dict:
        entity = await self.collection.find_one({"id": entity_id}, {"_id": 0})

        if not entity:
            raise ResourceNotFoundError(f"{self.collection_name} with id {entity_id} not found")

        return entity

    async def get_all(self) -> List[dict]:
        cursor = self.collection.find({}, {"_id": 0})
        return await cursor.to_list(length=None)

    async def create(self, **kwargs) -> dict:
        try:
            document = kwargs.copy()
            document["id"] = await self._get_next_id()

            await self.collection.insert_one(document)

            document.pop("_id", None)
            return document
        except DuplicateKeyError as e:
            raise ResourceAlreadyExistsError(f"{self.collection_name} already exists") from e

    async def update(self, entity_id: int, **kwargs) -> dict:
        try:
            updated_entity = await self.collection.find_one_and_update(
                {"id": entity_id},
                {"$set": kwargs},
                return_document=ReturnDocument.AFTER,
                projection={"_id": 0}
            )

            if not updated_entity:
                raise ResourceNotFoundError(f"{self.collection_name} with id {entity_id} not found")

            return updated_entity
        except DuplicateKeyError as e:
            raise ResourceAlreadyExistsError(f"{self.collection_name} already exists") from e

    async def delete(self, entity_id: int) -> None:
        result = await self.collection.delete_one({"id": entity_id})
        if result.deleted_count == 0:
            raise ResourceNotFoundError(f"{self.collection_name} with id {entity_id} not found")