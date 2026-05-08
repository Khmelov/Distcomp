from enum import IntEnum
from http import HTTPStatus


class ErrorStatus(IntEnum):
    NOT_FOUND = HTTPStatus.NOT_FOUND.value * 100 + 1
    ALREADY_EXIST = HTTPStatus.FORBIDDEN * 100 + 1