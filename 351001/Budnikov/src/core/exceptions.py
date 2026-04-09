class BaseAppException(Exception):
    def __init__(self, status_code: int, error_code: str, error_message: str):
        self.status_code = status_code
        self.error_code = error_code
        self.error_message = error_message

