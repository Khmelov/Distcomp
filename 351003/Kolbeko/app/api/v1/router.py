from fastapi import APIRouter
from app.api.v1.endpoints import authors, tweets, labels, notices

api_router = APIRouter()

api_router.include_router(authors.router, prefix="/authors", tags=["Authors"])
api_router.include_router(tweets.router, prefix="/tweets", tags=["Tweets"])
api_router.include_router(labels.router, prefix="/labels", tags=["Labels"])
api_router.include_router(notices.router, prefix="/notices", tags=["Notices"])