"""Global DRF exception handler producing ``{errorMessage, errorCode}`` bodies.

``errorCode`` is a five-digit number: first three digits equal the HTTP
status, last two are an offset identifying the specific error family.
"""
from rest_framework import status as drf_status
from rest_framework.response import Response
from rest_framework.views import exception_handler as drf_exception_handler


_OFFSET_BY_EXCEPTION = {
    "NotAuthenticated": 1,
    "AuthenticationFailed": 2,
    "PermissionDenied": 3,
    "NotFound": 4,
    "MethodNotAllowed": 5,
    "ValidationError": 6,
    "ParseError": 7,
    "UnsupportedMediaType": 8,
    "Throttled": 9,
}


def _flatten_detail(detail) -> str:
    if isinstance(detail, list):
        return "; ".join(_flatten_detail(d) for d in detail)
    if isinstance(detail, dict):
        parts = []
        for key, value in detail.items():
            parts.append(f"{key}: {_flatten_detail(value)}")
        return "; ".join(parts)
    return str(detail)


def build_error_code(http_status: int, offset: int = 0) -> int:
    """Return a five-digit error code (e.g. 40101)."""
    offset = max(0, min(99, offset))
    return http_status * 100 + offset


def error_response(http_status: int, message: str, offset: int = 0) -> Response:
    return Response(
        {
            "errorMessage": message,
            "errorCode": build_error_code(http_status, offset),
        },
        status=http_status,
    )


def api_exception_handler(exc, context):
    response = drf_exception_handler(exc, context)
    if response is None:
        # Unhandled server-side error: wrap as 500 so clients still get the
        # structured payload.
        return Response(
            {
                "errorMessage": str(exc) or "Internal server error",
                "errorCode": build_error_code(drf_status.HTTP_500_INTERNAL_SERVER_ERROR),
            },
            status=drf_status.HTTP_500_INTERNAL_SERVER_ERROR,
        )

    message = _flatten_detail(response.data) if response.data else exc.__class__.__name__
    offset = _OFFSET_BY_EXCEPTION.get(exc.__class__.__name__, 0)
    response.data = {
        "errorMessage": message,
        "errorCode": build_error_code(response.status_code, offset),
    }
    return response
