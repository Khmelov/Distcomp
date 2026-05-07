from fastapi import Request
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError

class AppException(Exception):
    def __init__(self, status_code: int, message: str, sub_code: int):
        self.status_code = status_code
        self.message = message
        self.error_code = f"{status_code}{sub_code:02d}"

async def app_exception_handler(request: Request, exc: AppException):
    return JSONResponse(
        status_code=exc.status_code,
        content={"errorMessage": exc.message, "errorCode": exc.error_code}
    )

async def validation_exception_handler(request: Request, exc: RequestValidationError):
    return JSONResponse(
        status_code=400,
        content={"errorMessage": "Validation failed", "errorCode": "40000"}
    )