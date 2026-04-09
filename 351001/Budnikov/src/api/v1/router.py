from fastapi import APIRouter
from fastapi.responses import JSONResponse

from src.api.v1.endpoints.editors import router as editor_router


api_router = APIRouter(prefix='/v1')

api_router.include_router(editor_router)


@api_router.get("/healthcheck")
async def healthcheck():
    return JSONResponse(
        status_code=200,
        content={"server": "ok"},
    )
