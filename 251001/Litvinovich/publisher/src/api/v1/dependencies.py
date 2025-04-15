from fastapi import Depends

from src.repositories.creators import CreatorsRepository
from src.repositories.markers import MarkersRepository
from src.repositories.reactions import ReactionsRepository
from src.repositories.stories import StoriesRepository
from src.services.creators import CreatorsService
from src.services.markers import MarkersService
from src.services.reactions import ReactionsService
from src.services.stories import StoriesService
from src.utils.redis_client import redis_client

creators_repository = CreatorsRepository()
stories_repository = StoriesRepository()
reactions_repository = ReactionsRepository()
markers_repository = MarkersRepository()


def get_creators_service():
    return CreatorsService(creators_repository)

def  get_stories_service():
    return StoriesService(stories_repository)

def get_reactions_service():
    return ReactionsService(redis_client)

def get_markers_service():
    return MarkersService(markers_repository)
