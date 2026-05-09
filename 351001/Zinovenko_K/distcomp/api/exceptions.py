from rest_framework.views import exception_handler
from rest_framework.response import Response
from rest_framework import status


def custom_exception_handler(exc, context):
    response = exception_handler(exc, context)

    if response is not None:
        http_code = response.status_code
        error_code = http_code * 100 + 1

        if isinstance(response.data, dict):
            detail = response.data.get('detail', str(response.data))
        elif isinstance(response.data, list):
            detail = '; '.join(str(item) for item in response.data)
        else:
            detail = str(response.data)

        response.data = {
            'errorMessage': str(detail),
            'errorCode': error_code,
        }

    return response


def error_response(message, http_status, error_code=None):
    if error_code is None:
        error_code = http_status * 100 + 1
    return Response(
        {'errorMessage': message, 'errorCode': error_code},
        status=http_status
    )
