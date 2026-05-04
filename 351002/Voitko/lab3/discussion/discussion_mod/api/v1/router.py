from fastapi import APIRouter

from discussion_mod.api.v1.endpoints import notes

router_v1 = APIRouter(prefix="/v1.0")
router_v1.include_router(notes.router, tags=["notes"])
