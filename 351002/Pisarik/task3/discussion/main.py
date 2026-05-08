from fastapi import FastAPI, Request, status
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from app.controllers.message_controller import router as message_router

# Cassandra connects lazily on first message request so the server binds immediately
# (avoids startup failure / empty TCP when Cassandra is still booting).

app = FastAPI(title="Discussion module (Cassandra)", version="1.0.0")


@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError) -> JSONResponse:
    return JSONResponse(
        status_code=status.HTTP_400_BAD_REQUEST,
        content={"errorMessage": "Validation error", "errorCode": 40001, "details": exc.errors()},
    )


@app.get("/api/v1.0/health")
async def health_check() -> dict:
    return {"status": "ok"}


app.include_router(message_router)


def get_app() -> FastAPI:
    return app
