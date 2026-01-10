from fastapi import APIRouter
from app.api import notes

api_router = APIRouter()

api_router.include_router(
    notes.router, 
    prefix="/api/v1.0", 
    tags=["notes"]
)
