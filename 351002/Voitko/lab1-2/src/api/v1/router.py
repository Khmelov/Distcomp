from fastapi import APIRouter

from src.api.v1.endpoints import writers, news, labels, notes

router_v1 = APIRouter(prefix="/v1.0")

router_v1.include_router(writers.router, tags=["writers"])
router_v1.include_router(news.router, tags=["news"])
router_v1.include_router(labels.router, tags=["labels"])
router_v1.include_router(notes.router, tags=["notes"])
