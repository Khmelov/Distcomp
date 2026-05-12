from fastapi import FastAPI

from src.core.errors.errors import ResourceNotFoundError, ResourceAlreadyExistsError
from src.core.errors.handlers import already_exist_handler, not_found_handler
from src.core.errors.messages import NoteErrorMessage, WriterErrorMessage, NewsErrorMessage


def register_error_handlers(app: FastAPI) -> None:
    app.add_exception_handler(ResourceNotFoundError, not_found_handler)
    app.add_exception_handler(ResourceAlreadyExistsError, already_exist_handler)