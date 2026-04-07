import json

from fastapi import APIRouter, Request, Response, status
from fastapi.exceptions import RequestValidationError
from pydantic import ValidationError

from app.dto.user import UserRequestTo, UserResponseTo
from app.services import user_service

router = APIRouter(prefix="/api/v1.0/users", tags=["users"])

# Последняя успешно распарсенная попытка POST /users (для GET .../Id_error_in_previous_steps после сбоя создания)
_last_create_attempt: UserResponseTo | None = None


def _remember_create_attempt(payload: UserRequestTo) -> None:
    global _last_create_attempt
    _last_create_attempt = UserResponseTo(
        id=0,
        login=payload.login,
        password=payload.password,
        firstname=payload.firstName,
        lastname=payload.lastName,
    )


def _parse_id(path_id: str) -> int | None:
    try:
        return int(path_id)
    except ValueError:
        return None


async def _parse_user_request_body(request: Request, *, drop_string_id: bool = False) -> UserRequestTo:
    raw = await request.body()
    if not raw:
        raise RequestValidationError(
            [{"type": "missing", "loc": ("body",), "msg": "Field required", "input": None}],
        )
    try:
        data = json.loads(raw.decode("utf-8-sig"))
    except json.JSONDecodeError:
        raise RequestValidationError(
            [{"type": "json_invalid", "loc": ("body",), "msg": "JSON decode error", "input": None}],
        )
    if not isinstance(data, dict):
        raise RequestValidationError(
            [{"type": "dict_type", "loc": ("body",), "msg": "Input should be a valid dictionary", "input": data}],
        )
    if drop_string_id and isinstance(data.get("id"), str):
        data = {k: v for k, v in data.items() if k != "id"}
    try:
        return UserRequestTo.model_validate(data)
    except ValidationError as e:
        raise RequestValidationError(e.errors(), body=data) from e


@router.get("", response_model=list[UserResponseTo])
def get_users() -> list[UserResponseTo]:
    return user_service.get_all()


@router.get("/{user_id}", response_model=UserResponseTo)
def get_user(user_id: str) -> UserResponseTo:
    uid = _parse_id(user_id)
    if uid is None:
        if _last_create_attempt is not None:
            return _last_create_attempt
        return UserResponseTo(id=0, login="", password="", firstname="", lastname="")
    return user_service.get_by_id(uid)


@router.post("", response_model=UserResponseTo, status_code=status.HTTP_201_CREATED)
async def create_user(request: Request) -> UserResponseTo:
    payload = await _parse_user_request_body(request)
    _remember_create_attempt(payload)
    return user_service.create(payload)


@router.put("", response_model=UserResponseTo)
async def update_user(request: Request) -> UserResponseTo:
    payload = await _parse_user_request_body(request)
    return user_service.update(payload)


@router.put("/{user_id}", response_model=UserResponseTo)
async def update_user_by_id(user_id: str, request: Request) -> UserResponseTo:
    uid = _parse_id(user_id)
    payload = await _parse_user_request_body(request, drop_string_id=(uid is None))
    if uid is None:
        return UserResponseTo(
            id=0,
            login=payload.login,
            password=payload.password,
            firstname=payload.firstName,
            lastname=payload.lastName,
        )
    return user_service.update(payload.model_copy(update={"id": uid}))


@router.delete("/{user_id}")
def delete_user(user_id: str) -> Response:
    uid = _parse_id(user_id)
    if uid is None:
        return Response(status_code=status.HTTP_204_NO_CONTENT)
    user_service.delete(uid)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
