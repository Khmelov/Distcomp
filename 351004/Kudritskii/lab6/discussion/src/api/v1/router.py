from fastapi import APIRouter

from src.api.v1.endpoints import news

router_v1 = APIRouter(prefix="/v1.0")

router_v1.include_router(news.router, labels=["newss"])