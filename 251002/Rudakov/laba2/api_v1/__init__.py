from fastapi import APIRouter
from .stories.views import router as stories_router 
from .creators.views import router as creators_router
from .messages.views import router as messages_router
from .stickers.views import router as stickers_router

router = APIRouter(
    prefix="/v1.0"
)

router.include_router(stories_router, tags=["Stories"])
router.include_router(creators_router, tags=["Creators"])
router.include_router(messages_router, tags=["Messages"])
router.include_router(stickers_router, tags=["Stickers"])
