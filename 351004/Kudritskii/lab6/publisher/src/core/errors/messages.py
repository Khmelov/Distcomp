from enum import Enum


class NoticeErrorMessage(str, Enum):
    NOT_FOUND = "Notice not found"

class UserErrorMessage(str, Enum):
    NOT_FOUND = "User not found"

class NewsErrorMessage(str, Enum):
    NOT_FOUND = "News not found"

class LabelErrorMessage(str, Enum):
    NOT_FOUND = "Label not found"