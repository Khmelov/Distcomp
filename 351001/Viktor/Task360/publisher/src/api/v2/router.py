from fastapi import APIRouter
from .endpoints import login, creators, tweets, markers, posts

router_v2 = APIRouter(prefix="/v2.0")

router_v2.include_router(login.router, tags=["login"])
router_v2.include_router(creators.router, tags=["creators_v2"])
router_v2.include_router(tweets.router, tags=["tweets_v2"])
router_v2.include_router(markers.router, tags=["markers_v2"])
router_v2.include_router(posts.router, tags=["posts_v2"])