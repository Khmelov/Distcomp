class ApiError(Exception):
    def __init__(self, status_code: int, error_code: int, error_message: str):
        self.status_code = status_code
        self.error_code = error_code
        self.error_message = error_message
        super().__init__(error_message)


class BadRequestError(ApiError):
    def __init__(self, error_message: str, suffix: int = 1):
        super().__init__(400, int(f"400{suffix:02d}"), error_message)


class NotFoundError(ApiError):
    def __init__(self, error_message: str, suffix: int = 1):
        super().__init__(404, int(f"404{suffix:02d}"), error_message)


class ConflictError(ApiError):
    def __init__(self, error_message: str, suffix: int = 1):
        super().__init__(409, int(f"409{suffix:02d}"), error_message)