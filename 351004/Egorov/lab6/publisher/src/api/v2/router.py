from fastapi import APIRouter

from src.api.v1.endpoints import author, topic, note, tag

router_v2 = APIRouter(prefix="/v2.0")

router_v2.include_router(author.router, tags=["authors"])
router_v2.include_router(topic.router, tags=["topics"])
router_v2.include_router(note.router, tags=["notes"])
router_v2.include_router(tag.router, tags=["tags"])