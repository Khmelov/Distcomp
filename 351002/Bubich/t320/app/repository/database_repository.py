from typing import List, Optional, Type, Dict, Any, Union
from app.models.database import db
from app.utils.query_utils import PaginationParams, paginate_query
from app.exceptions.custom_exceptions import DuplicateError
from sqlalchemy.exc import IntegrityError
import re


class DatabaseRepository:
    """Обобщенный репозиторий для работы с базой данных"""

    def __init__(self, model_class):
        self.model_class = model_class

    def save(self, entity: Union[Dict[str, Any], object]) -> object:
        """Создание новой записи с обработкой дубликатов"""
        try:
            if isinstance(entity, dict):
                model_instance = self.model_class(**entity)
            else:
                model_instance = entity

            db.session.add(model_instance)
            db.session.commit()
            return model_instance
        except IntegrityError as e:
            db.session.rollback()
            # Извлекаем информацию о нарушении уникальности
            error_msg = str(e.orig) if hasattr(e, 'orig') else str(e)

            # Пытаемся извлечь имя поля из ошибки
            field_name = "field"
            if "UNIQUE constraint failed" in error_msg:
                # Для SQLite
                match = re.search(r'tbl_writer\.(\w+)', error_msg)
                if match:
                    field_name = match.group(1)
                else:
                    # Пробуем другой формат
                    match = re.search(r'unique constraint.*?\((\w+)\)', error_msg.lower())
                    if match:
                        field_name = match.group(1)
            elif "duplicate key value violates unique constraint" in error_msg:
                # Для PostgreSQL
                match = re.search(r'Key \((\w+)\)=', error_msg)
                if match:
                    field_name = match.group(1)

            raise DuplicateError(
                f"Duplicate value for field '{field_name}'. This value already exists.",
                "40301"
            )
        except Exception as e:
            db.session.rollback()
            raise e

    # ... остальные методы остаются без изменений
    def find_by_id(self, id: int) -> Optional[object]:
        return self.model_class.query.get(id)

    def find_all(self, pagination: PaginationParams = None) -> Dict:
        query = self.model_class.query
        if pagination:
            return paginate_query(query, pagination)
        items = query.all()
        return {
            'items': items,
            'total': len(items),
            'page': 0,
            'size': len(items)
        }

    def update(self, id: int, entity: Union[Dict[str, Any], object]) -> Optional[object]:
        try:
            model_instance = self.find_by_id(id)
            if model_instance:
                if isinstance(entity, dict):
                    for key, value in entity.items():
                        if hasattr(model_instance, key):
                            setattr(model_instance, key, value)
                db.session.commit()
            return model_instance
        except IntegrityError as e:
            db.session.rollback()
            error_msg = str(e.orig) if hasattr(e, 'orig') else str(e)
            field_name = "field"
            match = re.search(r'tbl_writer\.(\w+)', error_msg)
            if match:
                field_name = match.group(1)
            raise DuplicateError(
                f"Duplicate value for field '{field_name}'.",
                "40301"
            )
        except Exception as e:
            db.session.rollback()
            raise e

    def delete_by_id(self, id: int) -> bool:
        """Удаление записи по ID с обработкой каскадных связей"""
        try:
            model_instance = self.find_by_id(id)
            if model_instance:
                # Проверяем, есть ли связанные записи
                # Для Writer - удаляем все связанные Story и их Comments
                if hasattr(model_instance, 'stories'):
                    for story in model_instance.stories:
                        # Удаляем комментарии к story
                        if hasattr(story, 'comments'):
                            for comment in story.comments:
                                db.session.delete(comment)
                        db.session.delete(story)

                db.session.delete(model_instance)
                db.session.commit()
                return True
            return False
        except Exception as e:
            db.session.rollback()
            raise e

    def find_by_field(self, field: str, value: Any) -> List[object]:
        return self.model_class.query.filter(
            getattr(self.model_class, field) == value
        ).all()

    def find_with_filters(self, filters: Dict[str, Any], pagination: PaginationParams = None) -> Dict:
        query = self.model_class.query
        for field, value in filters.items():
            if value is not None and hasattr(self.model_class, field):
                if isinstance(value, str):
                    query = query.filter(
                        getattr(self.model_class, field).ilike(f"%{value}%")
                    )
                elif isinstance(value, list):
                    query = query.filter(
                        getattr(self.model_class, field).in_(value)
                    )
                else:
                    query = query.filter(
                        getattr(self.model_class, field) == value
                    )
        if pagination:
            return paginate_query(query, pagination)
        items = query.all()
        return {
            'items': items,
            'total': len(items),
            'page': 0,
            'size': len(items)
        }

    def count(self, filters: Dict[str, Any] = None) -> int:
        query = self.model_class.query
        if filters:
            for field, value in filters.items():
                if value is not None and hasattr(self.model_class, field):
                    query = query.filter(getattr(self.model_class, field) == value)
        return query.count()