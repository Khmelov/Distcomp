from fastapi import Depends
from motor.motor_asyncio import AsyncIOMotorCollection, AsyncIOMotorDatabase

from src.core.database import get_db
from src.services import NewsService

def get_newss_collection(
    db: AsyncIOMotorDatabase = Depends(get_db),
) -> AsyncIOMotorCollection:
    return db["tbl_news"]

def get_news_service(
        session: AsyncIOMotorCollection = Depends(get_newss_collection)
) -> NewsService:
    return NewsService(session)