from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse

from .common.errors import ApiError
from .controllers.author_controller import router as author_router
from .controllers.issue_controller import router as issue_router
from .controllers.tag_controller import router as tag_router
from .controllers.comment_controller import router as comment_router

app = FastAPI(
    title="Task310 REST API",
    version="1.0"
)


@app.exception_handler(ApiError)
async def api_error_handler(request: Request, exc: ApiError):
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "errorMessage": exc.error_message,
            "errorCode": exc.error_code
        }
    )


@app.exception_handler(RequestValidationError)
async def request_validation_error_handler(request: Request, exc: RequestValidationError):
    messages = []
    for err in exc.errors():
        location = ".".join(str(item) for item in err.get("loc", []))
        msg = err.get("msg", "Validation error")
        messages.append(f"{location}: {msg}")

    return JSONResponse(
        status_code=400,
        content={
            "errorMessage": "; ".join(messages),
            "errorCode": 40001
        }
    )


app.include_router(author_router)
app.include_router(issue_router)
app.include_router(tag_router)
app.include_router(comment_router)