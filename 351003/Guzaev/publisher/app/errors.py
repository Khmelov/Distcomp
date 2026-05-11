from fastapi import Request
from fastapi.responses import JSONResponse

class AppError(Exception):
    def __init__(self, status_code: int, message: str, error_code: int):
        self.status_code = status_code
        self.message = message
        self.error_code = error_code

async def app_error_handler(request: Request, exc: AppError):
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "errorMessage": exc.message,
            "errorCode": exc.error_code
        }
    )
