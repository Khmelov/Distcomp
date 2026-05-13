from typing import List, Optional, Type, Dict, Any
from app.models.database import db
from app.utils.query_utils import PaginationParams, paginate_query


class DatabaseRepository:
    """Обобщенный репозиторий для работы с БД"""

    def __init__(self, model_class):
        self.model_class = model_class

    def save(self, entity: Dict[str, Any]) -> object:
        """Создание новой записи"""
        model_instance = self.model_class(**entity)
        db.session.add(model_instance)
        db.session.commit()
        return model_instance

    def find_by_id(self, id: int) -> Optional[object]:
        """Поиск по ID"""
        return self.model_class.query.get(id)

    def find_all(self, pagination: PaginationParams = None) -> Dict:
        """Получение всех записей с пагинацией"""
        query = self.model_class.query
        if pagination:
            return paginate_query(query, pagination)
        return {'items': query.all(), 'total': query.count()}

    def update(self, id: int, entity: Dict[str, Any]) -> Optional[object]:
        """Обновление записи"""
        model_instance = self.find_by_id(id)
        if model_instance:
            for key, value in entity.items():
                if hasattr(model_instance, key):
                    setattr(model_instance, key, value)
            db.session.commit()
        return model_instance

    def delete_by_id(self, id: int) -> bool:
        """Удаление записи по ID"""
        model_instance = self.find_by_id(id)
        if model_instance:
            db.session.delete(model_instance)
            db.session.commit()
            return True
        return False

    def find_by_field(self, field: str, value: Any) -> List[object]:
        """Поиск по полю"""
        return self.model_class.query.filter(getattr(self.model_class, field) == value).all()

    def find_with_filters(self, filters: Dict[str, Any], pagination: PaginationParams = None):
        """Поиск с фильтрами"""
        query = self.model_class.query
        for field, value in filters.items():
            if value is not None and hasattr(self.model_class, field):
                query = query.filter(getattr(self.model_class, field) == value)

        if pagination:
            return paginate_query(query, pagination)
        return {'items': query.all(), 'total': query.count()}