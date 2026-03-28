from fastapi import APIRouter
from app.api.v1.endpoints.editors import router as editors_router
from app.api.v1.endpoints.issues import router as issues_router
from app.api.v1.endpoints.markers import router as markers_router
from app.api.v1.endpoints.comments import router as comments_router

router = APIRouter()

router.include_router(editors_router)
router.include_router(issues_router)
router.include_router(markers_router)
router.include_router(comments_router)
