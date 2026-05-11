from typing import List, Optional
from datetime import datetime
from app.models.story import Story
from app.dto.requests.story_request import StoryRequestTo
from app.dto.responses.story_response import StoryResponseTo
from app.repository.in_memory_repository import InMemoryRepository
from app.exceptions.custom_exceptions import NotFoundError, ValidationError


class StoryService:
    def __init__(self, repository: InMemoryRepository):
        self.repository = repository

    def create(self, request: StoryRequestTo) -> StoryResponseTo:
        self._validate_request(request)
        now = datetime.utcnow()
        story = Story(
            writer_id=request.writer_id,
            title=request.title,
            content=request.content,
            created=now,
            modified=now
        )
        saved_story = self.repository.save(story)
        return self._to_response(saved_story)

    def get_by_id(self, id: int) -> StoryResponseTo:
        story = self.repository.find_by_id(id)
        if not story:
            raise NotFoundError(f"Story with id {id} not found")
        return self._to_response(story)

    def get_all(self) -> List[StoryResponseTo]:
        stories = self.repository.find_all()
        return [self._to_response(s) for s in stories]

    def update(self, id: int, request: StoryRequestTo) -> StoryResponseTo:
        self._validate_request(request)
        existing_story = self.repository.find_by_id(id)
        if not existing_story:
            raise NotFoundError(f"Story with id {id} not found")

        existing_story.writer_id = request.writer_id
        existing_story.title = request.title
        existing_story.content = request.content
        existing_story.modified = datetime.utcnow()

        updated_story = self.repository.update(existing_story)
        return self._to_response(updated_story)

    def delete(self, id: int) -> None:
        if not self.repository.delete_by_id(id):
            raise NotFoundError(f"Story with id {id} not found")

    def get_by_criteria(self, mark_names: Optional[List[str]] = None,
                        mark_ids: Optional[List[int]] = None,
                        writer_login: Optional[str] = None,
                        title: Optional[str] = None,
                        content: Optional[str] = None,
                        writer_repo: Optional[InMemoryRepository] = None,
                        mark_repo: Optional[InMemoryRepository] = None) -> List[StoryResponseTo]:
        stories = self.repository.find_all()
        filtered = []

        for story in stories:
            if title and title.lower() not in story.title.lower():
                continue
            if content and content.lower() not in story.content.lower():
                continue
            if writer_login and writer_repo:
                writer = writer_repo.find_by_id(story.writer_id)
                if not writer or writer.login != writer_login:
                    continue

            filtered.append(story)

        return [self._to_response(s) for s in filtered]

    def _validate_request(self, request: StoryRequestTo):
        if not request.title or len(request.title) < 2 or len(request.title) > 64:
            raise ValidationError("Title must be between 2 and 64 characters")
        if not request.content or len(request.content) < 4 or len(request.content) > 2048:
            raise ValidationError("Content must be between 4 and 2048 characters")

    def _to_response(self, story: Story) -> StoryResponseTo:
        return StoryResponseTo(
            id=story.id,
            writer_id=story.writer_id,
            title=story.title,
            content=story.content,
            created=story.created.isoformat() if story.created else None,
            modified=story.modified.isoformat() if story.modified else None
        )