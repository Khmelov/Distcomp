from fastapi import Request, status
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError
from starlette.exceptions import HTTPException as StarletteHTTPException

def get_error_code(http_status: int, subcode: int = 0):
    return int(f"{http_status}{subcode:02d}")

def http_exception_handler(request: Request, exc: StarletteHTTPException):
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "errorMessage": exc.detail,
            "errorCode": get_error_code(exc.status_code, 1),
        },
    )

def validation_exception_handler(request: Request, exc: RequestValidationError):
    return JSONResponse(
        status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
        content={
            "errorMessage": "Validation failed",
            "errorCode": get_error_code(422, 1),
            "details": exc.errors(),
        },
    )
