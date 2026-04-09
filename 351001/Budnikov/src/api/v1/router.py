from fastapi import APIRouter
from fastapi.responses import JSONResponse

from src.api.v1.endpoints.editors import router as editor_router
from src.api.v1.endpoints.labels import router as label_router
from src.api.v1.endpoints.posts import router as post_router
from src.api.v1.endpoints.issues import router as issue_router


api_router = APIRouter(prefix="/v1.0")

api_router.include_router(editor_router, tags=["Editors"])
api_router.include_router(label_router, tags=["Labels"])
api_router.include_router(post_router, tags=["Posts"])
api_router.include_router(issue_router, tags=["Issues"])


@api_router.get("/healthcheck")
async def healthcheck():
    return JSONResponse(
        status_code=200,
        content={"server": "ok"},
    )
