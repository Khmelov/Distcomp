from fastapi import Request
from fastapi.responses import JSONResponse

class AppError(Exception):
    def __init__(self, status_code: int, message: str, suffix: int = 1):
        self.status_code = status_code
        self.message = message
        self.suffix = suffix
        super().__init__(message)

async def app_error_handler(request: Request, exc: AppError):
    error_code = f"{exc.status_code}{exc.suffix:02d}"
    return JSONResponse(status_code=exc.status_code, content={"errorMessage": exc.message, "errorCode": error_code})
