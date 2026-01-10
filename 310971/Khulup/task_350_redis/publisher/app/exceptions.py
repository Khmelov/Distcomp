from fastapi.responses import JSONResponse
from fastapi.requests import Request
from fastapi import status
from fastapi.exception_handlers import http_exception_handler
from fastapi.exceptions import HTTPException


async def custom_http_exception_handler(request: Request, exc: HTTPException):
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "errorMessage": exc.detail,
            "errorCode": f"{exc.status_code}01"
        },
    )
