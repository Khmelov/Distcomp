from fastapi import APIRouter

from src.api.v1.endpoints import user, notice, news, label

router_v1 = APIRouter(prefix="/v1.0")

router_v1.include_router(user.router, labels=["users"])
router_v1.include_router(notice.router, labels=["notices"])
router_v1.include_router(news.router, labels=["newss"])
router_v1.include_router(label.router, labels=["labels"])