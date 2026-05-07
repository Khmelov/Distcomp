class AppError(Exception):
    pass

class ResourceAlreadyExistsError(AppError):
    pass

class ResourceNotFoundError(AppError):
    pass