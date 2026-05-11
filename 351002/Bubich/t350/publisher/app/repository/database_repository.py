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
            error_msg = str(e.orig) if hasattr(e, 'orig') else str(e)
            field_name = "field"
            match = re.search(r'(\w+)\.(\w+)', error_msg)
            if match:
                field_name = match.group(2)
            raise DuplicateError(
                f"Duplicate value for field '{field_name}'.",
                "40301"
            )
        except Exception as e:
            db.session.rollback()
            raise e

    def find_by_id(self, id: int) -> Optional[object]:
        return self.model_class.query.get(id)

    def find_all(self, pagination: PaginationParams = None) -> Dict:
        query = self.model_class.query
        if pagination:
            return paginate_query(query, pagination)
        items = query.all()
        return {'items': items, 'total': len(items), 'page': 0, 'size': len(items)}

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
            raise DuplicateError("Duplicate value.", "40301")
        except Exception as e:
            db.session.rollback()
            raise e

    def delete_by_id(self, id: int) -> bool:
        model_instance = self.find_by_id(id)
        if model_instance:
            db.session.delete(model_instance)
            db.session.commit()
            return True
        return False

    def find_by_field(self, field: str, value: Any) -> List[object]:
        return self.model_class.query.filter(
            getattr(self.model_class, field) == value
        ).all()