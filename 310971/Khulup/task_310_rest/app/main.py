from fastapi import FastAPI
from fastapi.exceptions import HTTPException
from app.api.v1.api import api_router
from app.exceptions import custom_http_exception_handler

app = FastAPI(title="REST API on Python", version="1.0.0")
app.include_router(api_router, prefix="/api/v1.0")

app.add_exception_handler(HTTPException, custom_http_exception_handler)