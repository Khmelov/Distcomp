class AppException(Exception):
    def __init__(self, message: str, code: int, status: int):
        self.message = message
        self.code = code
        self.status = status


class NotFoundException(AppException):
    def __init__(self, message: str, code: int):
        super().__init__(message, code, 404)
