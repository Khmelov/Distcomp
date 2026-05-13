from typing import Dict, Any
from app.models.story_model import StoryModel
from app.repository.database_repository import DatabaseRepository
from publisher.app.exceptions import NotFoundError, ValidationError


class CommentService:
    def __init__(self, repository: DatabaseRepository, story_repository: DatabaseRepository = None):
        self.repository = repository
        self.story_repository = story_repository

    def create(self, data: Dict[str, Any]):
        """Создание comment с проверкой существования story"""
        # Преобразуем storyId в story_id если нужно
        if 'storyId' in data:
            data['story_id'] = data.pop('storyId')

        self._validate(data)

        # Проверяем существование story
        story_id = data.get('story_id')
        if story_id:
            if self.story_repository:
                story = self.story_repository.find_by_id(story_id)
                if not story:
                    raise ValidationError(
                        f"Story with id {story_id} does not exist",
                        "40301"
                    )
            else:
                story = StoryModel.query.get(story_id)
                if not story:
                    raise ValidationError(
                        f"Story with id {story_id} does not exist",
                        "40301"
                    )

        return self.repository.save(data)

    def get_by_id(self, id: int):
        comment = self.repository.find_by_id(id)
        if not comment:
            raise NotFoundError(f"Comment with id {id} not found")
        return comment

    def get_all(self):
        return self.repository.find_all()

    def update(self, id: int, data: Dict[str, Any]):
        """Обновление comment"""
        if 'storyId' in data:
            data['story_id'] = data.pop('storyId')

        self._validate(data)

        existing_comment = self.repository.find_by_id(id)
        if not existing_comment:
            raise NotFoundError(f"Comment with id {id} not found")

        # Проверяем story если она изменена
        story_id = data.get('story_id')
        if story_id and story_id != existing_comment.story_id:
            if self.story_repository:
                story = self.story_repository.find_by_id(story_id)
                if not story:
                    raise ValidationError(
                        f"Story with id {story_id} does not exist",
                        "40301"
                    )

        return self.repository.update(id, data)

    def delete(self, id: int):
        if not self.repository.delete_by_id(id):
            raise NotFoundError(f"Comment with id {id} not found")

    def get_by_story_id(self, story_id: int):
        """Получение комментариев по ID story"""
        return self.repository.find_by_field('story_id', story_id)

    def _validate(self, data: Dict[str, Any]):
        if 'content' in data:
            if len(data['content']) < 2 or len(data['content']) > 2048:
                raise ValidationError(
                    "Content must be between 2 and 2048 characters",
                    "40002"
                )