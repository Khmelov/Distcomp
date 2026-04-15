from fastapi import APIRouter
from app.api.v1.endpoints.comments import router as comments_router

router = APIRouter()
router.include_router(comments_router)
