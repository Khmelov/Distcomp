import re
from dataclasses import dataclass


def camel_to_snake(name):
    """Преобразует camelCase в snake_case"""
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', name)
    return re.sub('([a-z0-9])([A-Z])', r'\1_\2', s1).lower()


def snake_to_camel(name):
    """Преобразует snake_case в camelCase"""
    components = name.split('_')
    return components[0] + ''.join(x.title() for x in components[1:])


class BaseRequestTo:
    """Базовый класс для всех Request DTO с поддержкой camelCase"""

    def __init__(self, **kwargs):
        # Преобразуем все camelCase ключи в snake_case
        converted_kwargs = {}
        for key, value in kwargs.items():
            snake_key = camel_to_snake(key)
            converted_kwargs[snake_key] = value

        # Устанавливаем атрибуты
        for key, value in converted_kwargs.items():
            if hasattr(self, key):
                setattr(self, key, value)