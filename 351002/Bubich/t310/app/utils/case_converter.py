import re
from typing import Any, Dict, List

def snake_to_camel(snake_str: str) -> str:
    """Преобразует snake_case в camelCase"""
    components = snake_str.split('_')
    return components[0] + ''.join(x.title() for x in components[1:])

def camel_to_snake(camel_str: str) -> str:
    """Преобразует camelCase в snake_case"""
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', camel_str)
    return re.sub('([a-z0-9])([A-Z])', r'\1_\2', s1).lower()

def convert_keys_to_camel(data: Any) -> Any:
    """Рекурсивно преобразует все ключи словаря в camelCase"""
    if isinstance(data, dict):
        new_dict = {}
        for key, value in data.items():
            camel_key = snake_to_camel(key) if '_' in key else key
            new_dict[camel_key] = convert_keys_to_camel(value)
        return new_dict
    elif isinstance(data, list):
        return [convert_keys_to_camel(item) for item in data]
    return data

def convert_keys_to_snake(data: Any) -> Any:
    """Рекурсивно преобразует все ключи словаря в snake_case"""
    if isinstance(data, dict):
        new_dict = {}
        for key, value in data.items():
            snake_key = camel_to_snake(key)
            new_dict[snake_key] = convert_keys_to_snake(value)
        return new_dict
    elif isinstance(data, list):
        return [convert_keys_to_snake(item) for item in data]
    return data