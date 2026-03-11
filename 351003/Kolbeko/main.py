import uvicorn

from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError

from app.core.exceptions import AppException, app_exception_handler, validation_exception_handler
from app.api.v1.router import api_router

from app.core.database import engine, Base 

app = FastAPI(title="Distibuted Computing Labs by Vlada Kolbeko, 351003", redirect_slashes=False)

app.add_exception_handler(AppException, app_exception_handler)
app.add_exception_handler(RequestValidationError, validation_exception_handler)

app.include_router(api_router, prefix="/api/v1.0")

@app.on_event("startup")
async def startup():
    async with engine.begin() as conn:
        pass

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=24110, reload=True)