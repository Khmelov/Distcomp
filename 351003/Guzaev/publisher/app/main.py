import uvicorn
from fastapi import FastAPI
from controllers import writer_controller, tweet_controller, marker_controller, comment_controller
from errors import AppError, app_error_handler

import traceback
from database import init_db
from fastapi.exceptions import RequestValidationError
from fastapi import Request
from fastapi.responses import JSONResponse

app = FastAPI(title="DistComp Task 310")
init_db()
# Подключаем все роутеры
app.include_router(writer_controller.router)
app.include_router(tweet_controller.router)
app.include_router(marker_controller.router)
app.include_router(comment_controller.router)

app.add_exception_handler(AppError, app_error_handler)


@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    return JSONResponse(
        status_code=400,
        content={
            "errorMessage": "Invalid request parameters",
            "errorCode": 40001
        }
    )

@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    traceback.print_exc()
    return JSONResponse(
        status_code=500,
        content={
            "errorMessage": f"Internal server error: {str(exc)}",
            "errorCode": 50000
        }
    )

@app.get("/")
def root():
    return {"message": "DistComp Task 320"}

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=24110, http="h11")
