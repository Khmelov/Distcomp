from fastapi import APIRouter

from app.api.v1.endpoints import authors, issues, stickers, notes

api_router = APIRouter()

api_router.include_router(authors.router,  prefix="/authors",  tags=["Authors"])
api_router.include_router(issues.router,   prefix="/issues",   tags=["Issues"])
api_router.include_router(stickers.router, prefix="/stickers", tags=["Stickers"])
api_router.include_router(notes.router,    prefix="/notes",    tags=["Notes"])
