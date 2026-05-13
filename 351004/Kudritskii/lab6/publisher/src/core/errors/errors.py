class AppError(Exception):
    pass

class ResourceAlreadyExistsError(AppError):
    pass

class ResourceNotFoundError(AppError):
    pass

class UnuserizedError(AppError):
    pass

class ForbiddenError(AppError):
    pass