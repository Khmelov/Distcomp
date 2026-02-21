from enum import IntEnum
from http import HTTPStatus

__all__ = ["ErrorStatus"]

class ErrorStatus(IntEnum):
    NOT_FOUND = HTTPStatus.NOT_FOUND.value * 100 + 1