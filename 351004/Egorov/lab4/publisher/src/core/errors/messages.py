from enum import Enum


class TopicErrorMessage(str, Enum):
    NOT_FOUND = "Topic not found"

class AuthorErrorMessage(str, Enum):
    NOT_FOUND = "Author not found"

class NoteErrorMessage(str, Enum):
    NOT_FOUND = "Note not found"

class TagErrorMessage(str, Enum):
    NOT_FOUND = "Tag not found"