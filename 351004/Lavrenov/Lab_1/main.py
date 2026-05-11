from fastapi import Request
from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError
from debug_middleware import SimpleDebugMiddleware
from middleware import LoggingMiddleware
from controllers import user, topic, marker, notice
from exceptions import (
    validation_exception_handler,
    value_error_handler,
    generic_exception_handler,
)

app = FastAPI(title="REST API", version="1.0.0")

app.add_middleware(SimpleDebugMiddleware)
# app.add_middleware(LoggingMiddleware)
# Обработчики исключений
app.add_exception_handler(RequestValidationError, validation_exception_handler)
app.add_exception_handler(ValueError, value_error_handler)
app.add_exception_handler(Exception, generic_exception_handler)

# Подключение роутеров с префиксом /api/v1.0
app.include_router(user.router, prefix="/api/v1.0")
app.include_router(topic.router, prefix="/api/v1.0")
app.include_router(marker.router, prefix="/api/v1.0")
app.include_router(notice.router, prefix="/api/v1.0")


# @app.post("/api/v1.0/debug")
# async def debug_post(request: Request):
#     body = await request.body()
#     return {
#         "headers": dict(request.headers),
#         "body": body.decode(errors="ignore"),
#         "method": request.method,
#     }


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="127.0.0.1", port=24110, http="h11")
