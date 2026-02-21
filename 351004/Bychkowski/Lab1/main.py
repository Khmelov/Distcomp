import uvicorn
from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError
from routers import router
from exceptions import AppError, app_exception_handler, validation_exception_handler

app = FastAPI(title="Task310 REST API")

app.include_router(router)

app.add_exception_handler(AppError, app_exception_handler)
app.add_exception_handler(RequestValidationError, validation_exception_handler)

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=24110, reload=True)