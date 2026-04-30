from fastapi import APIRouter
from api.v2.endpoints import authors, issues, auth

api_router = APIRouter()

api_router.include_router(auth.router, tags=["Auth"])
api_router.include_router(authors.router, prefix="/authors", tags=["Authors V2"])
api_router.include_router(issues.router, prefix="/issues", tags=["Issues V2"])