from fastapi import FastAPI

from src.core.errors.errors import HttpNotFoundError
from src.core.errors.handlers import not_found_handler
from src.core.errors.messages import NoteErrorMessage, AuthorErrorMessage, TopicErrorMessage


def register_error_handlers(app: FastAPI) -> None:
    app.add_exception_handler(HttpNotFoundError, not_found_handler)