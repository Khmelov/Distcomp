from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse


class EntityNotFoundException(Exception):
    def __init__(self, entity_name: str, entity_id: int):
        self.message = f"{entity_name} with id {entity_id} not found"
        self.error_code = 40401
        super().__init__(self.message)


class EntityValidationException(Exception):
    def __init__(self, message: str, error_code: int = 40001):
        self.message = message
        self.error_code = error_code
        super().__init__(self.message)


class EntityDuplicateException(Exception):
    def __init__(self, field_name: str, field_value: str):
        self.message = f"Duplicate value for '{field_name}': {field_value}"
        self.error_code = 40301
        super().__init__(self.message)


class GatewayTimeoutException(Exception):
    def __init__(self, message: str = "Upstream service did not respond in time"):
        self.message = message
        self.error_code = 50401
        super().__init__(self.message)


class AuthenticationException(Exception):
    def __init__(self, message: str = "Authentication required"):
        self.message = message
        self.error_code = 40101
        super().__init__(self.message)


class AuthorizationException(Exception):
    def __init__(self, message: str = "Access denied"):
        self.message = message
        self.error_code = 40302
        super().__init__(self.message)


def register_exception_handlers(app: FastAPI) -> None:
    @app.exception_handler(EntityNotFoundException)
    async def handle_not_found(_: Request, exc: EntityNotFoundException) -> JSONResponse:
        return JSONResponse(
            status_code=404,
            content={"errorMessage": exc.message, "errorCode": exc.error_code},
        )

    @app.exception_handler(EntityValidationException)
    async def handle_validation(_: Request, exc: EntityValidationException) -> JSONResponse:
        return JSONResponse(
            status_code=400,
            content={"errorMessage": exc.message, "errorCode": exc.error_code},
        )

    @app.exception_handler(EntityDuplicateException)
    async def handle_duplicate(_: Request, exc: EntityDuplicateException) -> JSONResponse:
        return JSONResponse(
            status_code=403,
            content={"errorMessage": exc.message, "errorCode": exc.error_code},
        )

    @app.exception_handler(GatewayTimeoutException)
    async def handle_gateway_timeout(_: Request, exc: GatewayTimeoutException) -> JSONResponse:
        return JSONResponse(
            status_code=504,
            content={"errorMessage": exc.message, "errorCode": exc.error_code},
        )

    @app.exception_handler(AuthenticationException)
    async def handle_authentication(_: Request, exc: AuthenticationException) -> JSONResponse:
        return JSONResponse(
            status_code=401,
            content={"errorMessage": exc.message, "errorCode": exc.error_code},
        )

    @app.exception_handler(AuthorizationException)
    async def handle_authorization(_: Request, exc: AuthorizationException) -> JSONResponse:
        return JSONResponse(
            status_code=403,
            content={"errorMessage": exc.message, "errorCode": exc.error_code},
        )

    @app.exception_handler(RequestValidationError)
    async def handle_request_validation(_: Request, exc: RequestValidationError) -> JSONResponse:
        parts: list[str] = []
        for err in exc.errors():
            loc = " -> ".join(str(x) for x in err.get("loc", ()))
            parts.append(f"{loc}: {err.get('msg', '')}")
        message = "; ".join(parts) if parts else str(exc)
        return JSONResponse(
            status_code=400,
            content={"errorMessage": message, "errorCode": 40002},
        )
