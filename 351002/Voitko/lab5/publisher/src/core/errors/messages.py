from enum import Enum


class NewsErrorMessage(str, Enum):
    NOT_FOUND = "News not found"

class WriterErrorMessage(str, Enum):
    NOT_FOUND = "Writer not found"

class NoteErrorMessage(str, Enum):
    NOT_FOUND = "Note not found"

class LabelErrorMessage(str, Enum):
    NOT_FOUND = "Label not found"