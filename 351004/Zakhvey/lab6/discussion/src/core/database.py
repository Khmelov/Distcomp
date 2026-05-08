from motor.motor_asyncio import AsyncIOMotorClient

from src.core.settings import settings

client: AsyncIOMotorClient | None = None
db = None

async def init_db():
    global client, db

    client = AsyncIOMotorClient(settings.mongo.get_database_url)
    db = client[settings.mongo.name]

async def close_db():
    client.close()


def get_db():
    return db