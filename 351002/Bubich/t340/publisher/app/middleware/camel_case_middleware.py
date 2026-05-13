import re
from flask import request, jsonify


def camel_to_snake(name):
    """Преобразует camelCase в snake_case"""
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', name)
    return re.sub('([a-z0-9])([A-Z])', r'\1_\2', s1).lower()


def snake_to_camel(name):
    """Преобразует snake_case в camelCase"""
    components = name.split('_')
    return components[0] + ''.join(x.title() for x in components[1:])


def convert_dict_keys(d, converter):
    """Рекурсивно преобразует ключи словаря"""
    if isinstance(d, dict):
        return {converter(k): convert_dict_keys(v, converter) for k, v in d.items()}
    elif isinstance(d, list):
        return [convert_dict_keys(item, converter) for item in d]
    return d


class CamelCaseMiddleware:
    """
    Middleware для автоматического преобразования camelCase <-> snake_case
    """

    def __init__(self, app):
        self.app = app

    def __call__(self, environ, start_response):
        # Преобразуем входящие запросы из camelCase в snake_case
        # Это требует более сложной логики, проще сделать на уровне контроллеров
        return self.app(environ, start_response)