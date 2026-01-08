from fastapi import APIRouter
from .issues.views import router as issues_router 
from .writers.views import router as writers_router
from .comments.views import router as сomments_router
from .markers.views import router as markers_router

router = APIRouter(
    prefix="/v1.0"
)

router.include_router(issues_router, tags=["Issues"])
router.include_router(writers_router, tags=["Writers"])
router.include_router(сomments_router, tags=["Comments"])
router.include_router(markers_router, tags=["Markers"])
