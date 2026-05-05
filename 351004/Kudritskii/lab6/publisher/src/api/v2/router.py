from fastapi import APIRouter
from fastapi.params import Depends

from src.api.v2.endpoints import user, notice, news, label, auth
from src.deps import get_active_user_login

router_v2 = APIRouter(
    prefix="/v2.0",
)

router_v2.include_router(auth.router, labels=["auth"])
router_v2.include_router(user.public_router, labels=["users"])
router_v2.include_router(user.router, labels=["users"], dependencies=[Depends(get_active_user_login)])
router_v2.include_router(notice.router, labels=["notices"], dependencies=[Depends(get_active_user_login)])
router_v2.include_router(news.router, labels=["newss"], dependencies=[Depends(get_active_user_login)])
router_v2.include_router(label.router, labels=["labels"], dependencies=[Depends(get_active_user_login)])