import sys
import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from tortoise.contrib.fastapi import register_tortoise

from src.api.v1.router import api_router
from src.core.exceptions import BaseAppException
from src.config import TORTOISE_CONFIG


@asynccontextmanager
async def lifespan(app: FastAPI):
    # await AsyncORM.create_tables() # Uncomment to use instead of migrations

    yield

    # actions after


def create_fastapi_app():
    app = FastAPI(lifespan=lifespan, redirect_slashes=False)

    app.include_router(api_router, prefix="/api")

    register_tortoise(
        app,
        config=TORTOISE_CONFIG,
        generate_schemas=True,
    )

    return app


def init_logger():
    logging.basicConfig(
        level=logging.INFO,
        format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
        handlers=[logging.FileHandler("app.log"), logging.StreamHandler(sys.stdout)],
    )
    logger = logging.getLogger(__name__)
    logger.info("Logger is initialized")


init_logger()

app = create_fastapi_app()


@app.exception_handler(BaseAppException)
async def app_exception_handler(request: Request, exc: BaseAppException):
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "errorCode": exc.error_code,
            "errorMessage": exc.error_message,
        },
    )


# @app.exception_handler(IntegrityError)
# async def integrity_error_handler(request: Request, exc: IntegrityError):
#     logging.error(f"Integrity Error: {str(exc)}")
#
#     return JSONResponse(
#         status_code=403,
#         content={
#             "errorCode": "40301",
#             "errorMessage": f"Resource already exists: {str(exc)}",
#         },
#     )
