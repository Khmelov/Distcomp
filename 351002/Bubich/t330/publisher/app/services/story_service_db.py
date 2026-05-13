from typing import Dict, Any
from datetime import datetime
from app.models.story_model import StoryModel
from app.models.writer_model import WriterModel
from app.repository.database_repository import DatabaseRepository
from app.utils.query_utils import PaginationParams, paginate_query
from publisher.app.exceptions import (
    NotFoundError,
    ValidationError,
    DuplicateError
)
from sqlalchemy.exc import IntegrityError


class StoryServiceDB:
    def __init__(self, repository: DatabaseRepository, writer_repository: DatabaseRepository = None):
        self.repository = repository
        self.writer_repository = writer_repository

    def create(self, data: Dict[str, Any]):
        """Создание story с проверками"""
        if 'writerId' in data:
            data['writer_id'] = data.pop('writerId')

        self._validate(data)

        writer_id = data.get('writer_id')

        # Проверка существования writer
        if writer_id is not None:
            writer = WriterModel.query.get(writer_id)
            if not writer:
                raise ValidationError(
                    f"Writer with id {writer_id} does not exist",
                    "40301"
                )

        # Проверка на дубликат title у этого writer
        title = data.get('title')
        if writer_id and title:
            existing = StoryModel.query.filter_by(
                writer_id=writer_id,
                title=title
            ).first()
            if existing:
                raise DuplicateError(
                    f"Story with title '{title}' already exists for writer {writer_id}",
                    "40302"
                )

        data['created'] = datetime.utcnow()
        data['modified'] = datetime.utcnow()

        try:
            return self.repository.save(data)
        except IntegrityError as e:
            raise DuplicateError(
                f"Duplicate story title for this writer",
                "40302"
            )

    def update(self, id: int, data: Dict[str, Any]):
        """Обновление story с проверками"""
        if 'writerId' in data:
            data['writer_id'] = data.pop('writerId')

        self._validate(data)

        existing_story = self.repository.find_by_id(id)
        if not existing_story:
            raise NotFoundError(f"Story with id {id} not found")

        writer_id = data.get('writer_id', existing_story.writer_id)
        title = data.get('title', existing_story.title)

        # Проверка существования writer
        if writer_id is not None:
            writer = WriterModel.query.get(writer_id)
            if not writer:
                raise ValidationError(
                    f"Writer with id {writer_id} does not exist",
                    "40301"
                )

        # Проверка на дубликат title (исключая текущую запись)
        if title and (writer_id != existing_story.writer_id or title != existing_story.title):
            duplicate = StoryModel.query.filter(
                StoryModel.writer_id == writer_id,
                StoryModel.title == title,
                StoryModel.id != id
            ).first()
            if duplicate:
                raise DuplicateError(
                    f"Story with title '{title}' already exists for writer {writer_id}",
                    "40302"
                )

        data['modified'] = datetime.utcnow()

        try:
            return self.repository.update(id, data)
        except IntegrityError:
            raise DuplicateError(
                f"Duplicate story title for this writer",
                "40302"
            )

    # ... остальные методы без изменений
    def get_by_id(self, id: int):
        story = self.repository.find_by_id(id)
        if not story:
            raise NotFoundError(f"Story with id {id} not found")
        return story

    def get_all(self, pagination: PaginationParams = None):
        return self.repository.find_all(pagination)

    def delete(self, id: int):
        if not self.repository.delete_by_id(id):
            raise NotFoundError(f"Story with id {id} not found")

    def get_by_criteria(self, filters: Dict[str, Any], pagination: PaginationParams = None):
        query = StoryModel.query

        if filters.get('writerId'):
            query = query.filter(StoryModel.writer_id == filters['writerId'])
        if filters.get('title'):
            query = query.filter(StoryModel.title.ilike(f"%{filters['title']}%"))
        if filters.get('content'):
            query = query.filter(StoryModel.content.ilike(f"%{filters['content']}%"))
        if filters.get('writerLogin'):
            query = query.join(WriterModel).filter(WriterModel.login == filters['writerLogin'])

        if pagination:
            return paginate_query(query, pagination)

        total = query.count()
        return {'items': query.all(), 'total': total}

    def _validate(self, data: Dict[str, Any]):
        if 'title' in data:
            if len(data['title']) < 2 or len(data['title']) > 64:
                raise ValidationError("Title must be between 2 and 64 characters", "40002")
        if 'content' in data:
            if len(data['content']) < 4 or len(data['content']) > 2048:
                raise ValidationError("Content must be between 4 and 2048 characters", "40003")