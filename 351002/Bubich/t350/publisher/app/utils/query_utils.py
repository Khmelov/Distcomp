from typing import Tuple, List, Any, Optional
from flask import request


class PaginationParams:
    """Параметры пагинации и сортировки"""

    def __init__(self, page: int = 0, size: int = 10, sort_by: str = 'id',
                 sort_dir: str = 'asc'):
        self.page = max(0, page)
        self.size = min(max(1, size), 100)  # ограничение 1-100
        self.sort_by = sort_by
        self.sort_dir = 'asc' if sort_dir.lower() == 'asc' else 'desc'

    @property
    def offset(self) -> int:
        return self.page * self.size

    @property
    def limit(self) -> int:
        return self.size


def get_pagination_params() -> PaginationParams:
    """Извлечение параметров пагинации из запроса"""
    page = request.args.get('page', 0, type=int)
    size = request.args.get('size', 10, type=int)
    sort_by = request.args.get('sortBy', 'id')
    sort_dir = request.args.get('sortDir', 'asc')
    return PaginationParams(page, size, sort_by, sort_dir)


def paginate_query(query, pagination: PaginationParams):
    """Применяет пагинацию и сортировку к запросу"""
    # Сортировка
    if pagination.sort_dir == 'asc':
        query = query.order_by(pagination.sort_by)
    else:
        query = query.order_by(getattr(query.column_descriptions[0]['type'], pagination.sort_by).desc())

    # Пагинация
    total = query.count()
    items = query.offset(pagination.offset).limit(pagination.limit).all()

    return {
        'items': items,
        'total': total,
        'page': pagination.page,
        'size': pagination.size,
        'pages': (total + pagination.size - 1) // pagination.size
    }