from enum import IntEnum
from starlette import status

__all__ = ["ErrorStatus"]


class ErrorStatus(IntEnum):
    NOT_FOUND = status.HTTP_404_NOT_FOUND * 100 + 1
    ALREADY_EXIST = status.HTTP_403_FORBIDDEN * 100 + 1
    UNAUTHORIZED = status.HTTP_401_UNAUTHORIZED * 100 + 1
    FORBIDDEN = status.HTTP_403_FORBIDDEN * 100 + 2