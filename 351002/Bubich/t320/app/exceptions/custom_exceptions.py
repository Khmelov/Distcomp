class ValidationError(Exception):
    def __init__(self, message: str, error_code: str = "40001"):
        self.message = message
        self.error_code = error_code
        super().__init__(self.message)

class NotFoundError(Exception):
    def __init__(self, message: str, error_code: str = "40401"):
        self.message = message
        self.error_code = error_code
        super().__init__(self.message)

class DuplicateError(Exception):
    """Ошибка дублирования данных (403)"""
    def __init__(self, message: str, error_code: str = "40301"):
        self.message = message
        self.error_code = error_code
        super().__init__(self.message)