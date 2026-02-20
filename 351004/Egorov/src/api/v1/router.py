from fastapi import APIRouter

from src.api.v1.endpoints import author, topic, note, tag

router_v1 = APIRouter(prefix="/v1.0")

router_v1.include_router(author.router, tags=["authors"])
router_v1.include_router(topic.router, tags=["topics"])
router_v1.include_router(note.router, tags=["notes"])
router_v1.include_router(tag.router, tags=["tags"])