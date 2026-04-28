from fastapi import FastAPI

from publisher.config import get_settings
from publisher.exceptions.handlers import register_exception_handlers
from publisher.routers.creators import router as creators_router
from publisher.routers.markers import router as markers_router
from publisher.routers.stories import router as stories_router
from publisher.routers.notices import router as notices_router


def create_app() -> FastAPI:
    settings = get_settings()
    app = FastAPI(title=settings.app_name, version=settings.app_version)

    @app.get(f"{settings.api_prefix}/health")
    async def health() -> dict:
        return {"status": "ok"}

    register_exception_handlers(app)

    app.include_router(creators_router)
    app.include_router(markers_router)
    app.include_router(stories_router)
    app.include_router(notices_router)

    return app

