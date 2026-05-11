from app.routers.issue import router as issue_router
from app.routers.notice import router as notice_router
from app.routers.sticker import router as sticker_router
from app.routers.user import router as user_router
from app.routers.v2_auth import router as v2_auth_router
from app.routers.v2_issue import router as v2_issue_router
from app.routers.v2_notice import router as v2_notice_router
from app.routers.v2_sticker import router as v2_sticker_router
from app.routers.v2_user import router as v2_user_router

__all__ = [
    "user_router",
    "issue_router",
    "sticker_router",
    "notice_router",
    "v2_auth_router",
    "v2_user_router",
    "v2_issue_router",
    "v2_sticker_router",
    "v2_notice_router",
]
