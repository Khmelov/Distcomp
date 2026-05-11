from contextlib import asynccontextmanager
from fastapi import FastAPI
from sqlalchemy.exc import IntegrityError
from fastapi.exceptions import RequestValidationError
from debug_middleware import SimpleDebugMiddleware
from dependencies import init_db
from controllers import user, topic, marker, notice
from exceptions import (
    validation_exception_handler,
    value_error_handler,
    generic_exception_handler,
    integrity_error_handler,
)


@asynccontextmanager
async def lifespan(app: FastAPI):
    # startup
    init_db()
    yield
    # shutdown (можно добавить очистку)


app = FastAPI(title="REST API", version="1.0.0", lifespan=lifespan)

app.add_middleware(SimpleDebugMiddleware)
app.add_exception_handler(RequestValidationError, validation_exception_handler)
app.add_exception_handler(ValueError, value_error_handler)
app.add_exception_handler(IntegrityError, integrity_error_handler)
app.add_exception_handler(Exception, generic_exception_handler)


app.include_router(user.router, prefix="/api/v1.0")
app.include_router(topic.router, prefix="/api/v1.0")
app.include_router(marker.router, prefix="/api/v1.0")
app.include_router(notice.router, prefix="/api/v1.0")

if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="127.0.0.1", port=24110, http="h11")
