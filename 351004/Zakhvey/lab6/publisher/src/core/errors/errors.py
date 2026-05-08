class AppError(Exception):
    pass

class ResourceAlreadyExistsError(AppError):
    pass

class ResourceNotFoundError(AppError):
    pass

class UnauthorizedError(AppError):
    pass

class ForbiddenError(AppError):
    pass