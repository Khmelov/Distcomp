from fastapi import APIRouter

from src.api.v1.endpoints import writer, news, note, label

router_v1 = APIRouter(prefix="/v1.0")

router_v1.include_router(writer.router, tags=["writers"])
router_v1.include_router(news.router, tags=["news"])
router_v1.include_router(note.router, tags=["notes"])
router_v1.include_router(label.router, tags=["labels"])
