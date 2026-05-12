"""Разбор JSON-тела: плоский DTO или обёртка {\"writer\": {...}} (формат части клиентов/тренажёра)."""
from __future__ import annotations

from typing import Any, TypeVar

from pydantic import BaseModel

T = TypeVar("T", bound=BaseModel)


def parse_wrapped(body: dict[str, Any], keys: tuple[str, ...], model: type[T]) -> T:
    for k in keys:
        inner = body.get(k)
        if isinstance(inner, dict):
            return model.model_validate(inner)
    return model.model_validate(body)
