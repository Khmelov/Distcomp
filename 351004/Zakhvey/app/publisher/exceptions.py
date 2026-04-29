from fastapi import Request, HTTPException
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError

class AppError(HTTPException):
    def __init__(self, status_code: int, error_code: int, message: str):
        super().__init__(status_code=status_code, detail=message)
        self.error_code = error_code

async def app_exception_handler(request: Request, exc: AppError):
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "errorMessage": exc.detail,
            "errorCode": exc.error_code
        }
    )

async def validation_exception_handler(request: Request, exc: RequestValidationError):
    # Формируем ошибку валидации в требуемом формате
    error_msg = "; ".join([f"{err['loc'][-1]}: {err['msg']}" for err in exc.errors()])
    return JSONResponse(
        status_code=400,
        content={
            "errorMessage": f"Validation error: {error_msg}",
            "errorCode": 40001
        }
    )