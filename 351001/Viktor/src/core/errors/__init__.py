from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError
from src.core.errors.handlers import not_found_handler, validation_exception_handler
from src.core.errors.errors import HttpNotFoundError
from src.core.errors.handlers import not_found_handler
from src.core.errors.messages import PostErrorMessage, CreatorErrorMessage, TweetErrorMessage


def register_error_handlers(app: FastAPI) -> None:
    app.add_exception_handler(HttpNotFoundError, not_found_handler)
    app.add_exception_handler(RequestValidationError, validation_exception_handler)

