from src.core.constants import ErrorStatus


class HttpError(Exception):
    def __init__(self, message: str, error_code: ErrorStatus):
        super().__init__(message)
        self.error_code = error_code

class HttpNotFoundError(HttpError):
    pass