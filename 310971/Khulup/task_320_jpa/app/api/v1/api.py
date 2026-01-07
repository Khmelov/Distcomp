from fastapi import APIRouter
from app.api.v1.endpoints import users, issues, notes, markers

api_router = APIRouter()
api_router.include_router(users.router, prefix="/users", tags=["users"])
api_router.include_router(issues.router, prefix="/issues", tags=["issues"])
api_router.include_router(notes.router, prefix="/notes", tags=["notes"])
api_router.include_router(markers.router, prefix="/markers", tags=["markers"])
