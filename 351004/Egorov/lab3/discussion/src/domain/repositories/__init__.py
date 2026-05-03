from motor.motor_asyncio import AsyncIOMotorCollection

from src.domain.repositories.mongoDb import MongoRepository

class NoteRepository(MongoRepository):
    def __init__(self, collection: AsyncIOMotorCollection):
        super().__init__(collection)