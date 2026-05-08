from fastapi import FastAPI

from src.core.errors.errors import ResourceNotFoundError, ResourceAlreadyExistsError, UnuserizedError, ForbiddenError
from src.core.errors.handlers import already_exist_handler, not_found_handler, unuserized_handler, forbidden_handler
from src.core.errors.messages import NewsErrorMessage, UserErrorMessage, NoticeErrorMessage


def register_error_handlers(app: FastAPI) -> None:
    app.add_exception_handler(ResourceNotFoundError, not_found_handler)
    app.add_exception_handler(ResourceAlreadyExistsError, already_exist_handler)
    app.add_exception_handler(UnuserizedError, unuserized_handler)
    app.add_exception_handler(ForbiddenError, forbidden_handler)