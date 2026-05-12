from fastapi import APIRouter
from Task350.publisher.src.api.v1.endpoints import marker, creator, post, tweet

router_v1 = APIRouter(prefix="/v1.0")

router_v1.include_router(creator.router, tags=["creators"])
router_v1.include_router(tweet.router, tags=["tweets"])
router_v1.include_router(post.router, tags=["posts"])
router_v1.include_router(marker.router, tags=["markers"])