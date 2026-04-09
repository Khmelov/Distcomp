import asyncio
from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError
from routers import router
from exceptions import AppError, app_exception_handler, validation_exception_handler
from database import init_db
from hypercorn.config import Config
from hypercorn.asyncio import serve

init_db()

app = FastAPI(title="Entities App REST API")

app.include_router(router)
app.add_exception_handler(AppError, app_exception_handler)
app.add_exception_handler(RequestValidationError, validation_exception_handler)

if __name__ == "__main__":
   
    config = Config()
    
    config.bind = ["0.0.0.0:24110"] 
    
    
    asyncio.run(serve(app, config))