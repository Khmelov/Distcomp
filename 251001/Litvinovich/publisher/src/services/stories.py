from fastapi import HTTPException

from src.models.models import Story
from src.schemas.stories import StoryRequestToAdd, StoryResponseTo, StoryRequestToUpdate
from src.utils.repository import AbstractRepository, NotFoundError


class StoriesService:
    def __init__(self, stories_repo: AbstractRepository):
        self.stories_repo: AbstractRepository = stories_repo

    async def create_story(self, story: StoryRequestToAdd) -> StoryResponseTo:
        story_model = Story(
            title=story.title,
            content=story.content,
            creator_id=story.creator_id,
        )

        try:
            created_model = await self.stories_repo.create(story_model)
            return StoryResponseTo.model_validate(created_model)
        except ValueError as e:
            raise HTTPException(status_code=403, detail=str(e))

    async def get_stories(self) -> list[StoryResponseTo]:
        stories = await self.stories_repo.get_all()
        return [StoryResponseTo.model_validate(story) for story in stories]

    async def get_story_by_id(self, story_id: int) -> StoryResponseTo:
        try:
            story = await self.stories_repo.get_by_id(story_id)
            return StoryResponseTo.model_validate(story)
        except NotFoundError:
            raise HTTPException(status_code=404, detail="Story not found")

    async def delete_story(self, story_id: int):
        try:
            await self.stories_repo.delete(story_id)
        except NotFoundError:
            raise HTTPException(status_code=404, detail="Story not found")

    async def update_story(self, story: StoryRequestToUpdate) -> StoryResponseTo:
        try:
            existing_story = await self.stories_repo.get_by_id(story.id)

            existing_story.title = story.title
            existing_story.content = story.content
            existing_story.creator_id = story.creator_id

            updated_story = await self.stories_repo.update(existing_story)
            return StoryResponseTo.model_validate(updated_story)
        except NotFoundError:
            raise HTTPException(status_code=404, detail="Story not found")