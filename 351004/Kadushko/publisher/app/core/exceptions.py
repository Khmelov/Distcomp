from fastapi import HTTPException


class EntityNotFoundException(HTTPException):
    def __init__(self, entity: str, entity_id: int):
        super().__init__(
            status_code=404,
            detail={
                "errorMessage": f"{entity} with id={entity_id} not found",
                "errorCode": 40401
            }
        )


class EntityAlreadyExistsException(HTTPException):
    def __init__(self, entity: str, field: str, value: str):
        super().__init__(
            status_code=403,
            detail={
                "errorMessage": f"{entity} with {field}='{value}' already exists",
                "errorCode": 40301
            }
        )


class ValidationException(HTTPException):
    def __init__(self, message: str):
        super().__init__(
            status_code=400,
            detail={
                "errorMessage": message,
                "errorCode": 40001
            }
        )
