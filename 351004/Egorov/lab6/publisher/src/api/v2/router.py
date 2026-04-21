from fastapi import APIRouter
from fastapi.params import Depends

from src.api.v2.endpoints import author, topic, note, tag, auth
from src.deps import get_active_user_login

router_v2 = APIRouter(
    prefix="/v2.0",
)

router_v2.include_router(auth.router, tags=["auth"])
router_v2.include_router(author.public_router, tags=["authors"])
router_v2.include_router(author.router, tags=["authors"], dependencies=[Depends(get_active_user_login)])
router_v2.include_router(topic.router, tags=["topics"], dependencies=[Depends(get_active_user_login)])
router_v2.include_router(note.router, tags=["notes"], dependencies=[Depends(get_active_user_login)])
router_v2.include_router(tag.router, tags=["tags"], dependencies=[Depends(get_active_user_login)])