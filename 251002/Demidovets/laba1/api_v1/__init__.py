from fastapi import APIRouter
from .news.views import router as news_router 
from .editors.views import router as editors_router
from .comments.views import router as comments_router
from .stickers.views import router as stickers_router

router = APIRouter(
    prefix="/v1.0"
)

router.include_router(news_router, tags=["News"])
router.include_router(editors_router, tags=["Editors"])
router.include_router(comments_router, tags=["Comments"])
router.include_router(stickers_router, tags=["Stickers"])
