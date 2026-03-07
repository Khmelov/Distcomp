from fastapi import FastAPI

from lab1.src.core.errors.errors import HttpNotFoundError
from lab1.src.core.errors.handlers import not_found_handler
from lab1.src.core.errors.messages import NoteErrorMessage, AuthorErrorMessage, TopicErrorMessage


def register_error_handlers(app: FastAPI) -> None:
    app.add_exception_handler(HttpNotFoundError, not_found_handler)