from fastapi import FastAPI

from src.core.errors.errors import ResourceNotFoundError, ResourceAlreadyExistsError, UnauthorizedError, ForbiddenError
from src.core.errors.handlers import already_exist_handler, not_found_handler, unauthorized_handler, forbidden_handler
from src.core.errors.messages import NoteErrorMessage, AuthorErrorMessage, TopicErrorMessage


def register_error_handlers(app: FastAPI) -> None:
    app.add_exception_handler(ResourceNotFoundError, not_found_handler)
    app.add_exception_handler(ResourceAlreadyExistsError, already_exist_handler)
    app.add_exception_handler(UnauthorizedError, unauthorized_handler)
    app.add_exception_handler(ForbiddenError, forbidden_handler)