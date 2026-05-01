from rest_framework import status
from rest_framework.decorators import api_view, authentication_classes, permission_classes
from rest_framework.response import Response

from apps.security.authentication import JWTAuthentication
from apps.security.exceptions import build_error_code
from apps.security.jwt_utils import generate_token
from apps.security.passwords import verify_password
from apps.security.permissions import IsAuthenticatedJWT
from apps.security.serializers import LoginRequestSerializer
from apps.writers.models import Writer


def _invalid_credentials_response():
    # Return 401 directly — DRF's `AuthenticationFailed` gets downgraded to
    # 403 when the view has no authenticator, which we don't want here.
    return Response(
        {
            "errorMessage": "Invalid credentials",
            "errorCode": build_error_code(status.HTTP_401_UNAUTHORIZED, 2),
        },
        status=status.HTTP_401_UNAUTHORIZED,
    )


@api_view(["POST"])
@authentication_classes([])
@permission_classes([])
def login_view(request):
    serializer = LoginRequestSerializer(data=request.data)
    serializer.is_valid(raise_exception=True)
    login = serializer.validated_data["login"]
    password = serializer.validated_data["password"]

    try:
        writer = Writer.objects.get(login=login)
    except Writer.DoesNotExist:
        return _invalid_credentials_response()

    if not verify_password(password, writer.password):
        return _invalid_credentials_response()

    token_payload = generate_token(login=writer.login, role=writer.role)
    return Response(token_payload, status=status.HTTP_200_OK)


@api_view(["GET"])
@authentication_classes([JWTAuthentication])
@permission_classes([IsAuthenticatedJWT])
def me_view(request):
    user = request.user
    return Response(
        {
            "id": user.id,
            "login": user.login,
            "firstname": user.writer.firstname,
            "lastname": user.writer.lastname,
            "role": user.role,
        },
        status=status.HTTP_200_OK,
    )
