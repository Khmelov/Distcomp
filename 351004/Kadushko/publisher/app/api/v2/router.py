from fastapi import APIRouter
from app.api.v2.endpoints.auth import router as auth_router
from app.api.v2.endpoints.editors import router as editors_router
from app.api.v2.endpoints.issues import router as issues_router
from app.api.v2.endpoints.markers import router as markers_router
from app.api.v2.endpoints.comments import router as comments_router

router = APIRouter()
router.include_router(auth_router)
router.include_router(editors_router)
router.include_router(issues_router)
router.include_router(markers_router)
router.include_router(comments_router)
