from fastapi import APIRouter
from fastapi.params import Depends

from src.api.v2.endpoints import writer, news, note, label, auth
from src.deps import get_active_user_login

router_v2 = APIRouter(
    prefix="/v2.0",
)

router_v2.include_router(auth.router, tags=["auth"])
router_v2.include_router(writer.public_router, tags=["writers"])
router_v2.include_router(writer.router, tags=["writers"], dependencies=[Depends(get_active_user_login)])
router_v2.include_router(news.router, tags=["news"], dependencies=[Depends(get_active_user_login)])
router_v2.include_router(note.router, tags=["notes"], dependencies=[Depends(get_active_user_login)])
router_v2.include_router(label.router, tags=["labels"], dependencies=[Depends(get_active_user_login)])
