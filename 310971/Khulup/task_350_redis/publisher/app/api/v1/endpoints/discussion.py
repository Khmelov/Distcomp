from fastapi import APIRouter, Depends
import logging

from app.services.discussion_service import DiscussionService

router = APIRouter()

def get_discussion_service():
    return DiscussionService()

@router.get("/health")
async def discussion_health_check(
    discussion_service: DiscussionService = Depends(get_discussion_service)
):
    try:
        health = await discussion_service.health_check()
        return health
    except Exception as e:
        logging.error(f"Error checking discussion health: {e}")
        return {
            "status": "unhealthy",
            "error": str(e)
        }
