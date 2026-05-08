from datetime import datetime
from typing import Generic, List, Optional, TypeVar

from pydantic import BaseModel, Field


T = TypeVar("T")


class PaginationParams(BaseModel):
    page: int = Field(default=1, ge=1)
    size: int = Field(default=10, ge=1, le=100)
    sort_by: Optional[str] = None
    sort_order: str = Field(default="asc", pattern="^(asc|desc)$")


class Page(BaseModel, Generic[T]):
    items: List[T]
    total: int
    page: int
    size: int


class Timestamped(BaseModel):
    created_at: datetime

