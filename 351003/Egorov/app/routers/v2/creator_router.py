from typing import Any, Dict, List, Optional

from fastapi import APIRouter, Body, Depends, HTTPException, Query, status
from pydantic import ValidationError

from app.auth.dependencies import get_current_user
from app.dtos.creator_request import CreatorRequestTo
from app.dtos.creator_response import CreatorResponseTo
from app.models.creator import Creator, CreatorRole
from app.services.creator_service import CreatorService


router = APIRouter(prefix="/api/v2.0/creators", tags=["v2-creators"])


def get_creator_service() -> CreatorService:
    from main import creator_service

    return creator_service


def _is_admin(user: Creator) -> bool:
    return user.role == CreatorRole.ADMIN


@router.get("", response_model=List[CreatorResponseTo])
def list_creators(
    service: CreatorService = Depends(get_creator_service),
    _: Creator = Depends(get_current_user),
) -> List[CreatorResponseTo]:
    return service.get_all_creators()


@router.get("/{creator_id}", response_model=CreatorResponseTo)
def get_creator(
    creator_id: int,
    service: CreatorService = Depends(get_creator_service),
    _: Creator = Depends(get_current_user),
) -> CreatorResponseTo:
    creator = service.get_creator(creator_id)
    if not creator:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Creator not found")
    return creator


@router.put("/{creator_id}", response_model=CreatorResponseTo)
def update_creator(
    creator_id: int,
    raw_body: Optional[Dict[str, Any]] = Body(default=None),
    login: Optional[str] = Query(default=None),
    firstname: Optional[str] = Query(default=None),
    lastname: Optional[str] = Query(default=None),
    name: Optional[str] = Query(default=None),
    email: Optional[str] = Query(default=None),
    service: CreatorService = Depends(get_creator_service),
    current_user: Creator = Depends(get_current_user),
) -> CreatorResponseTo:
    if not _is_admin(current_user) and current_user.id != creator_id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Forbidden")

    data = dict(raw_body or {})
    login_val = data.get("login") or login
    name_val = (
        data.get("name")
        or name
        or " ".join(part for part in [data.get("firstname") or firstname, data.get("lastname") or lastname] if part).strip()
    )
    email_val = data.get("email") or email or (f"{login_val}@example.com" if login_val else None)

    if not (login_val and name_val and email_val):
        raise HTTPException(status_code=status.HTTP_422_UNPROCESSABLE_ENTITY, detail="Validation error")

    try:
        dto = CreatorRequestTo(login=login_val, name=name_val, email=email_val)
    except ValidationError as exc:
        raise HTTPException(status_code=status.HTTP_422_UNPROCESSABLE_ENTITY, detail=str(exc)) from exc

    updated = service.update_creator(creator_id, dto)
    if not updated:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Creator not found")
    return updated


@router.delete("/{creator_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_creator(
    creator_id: int,
    service: CreatorService = Depends(get_creator_service),
    current_user: Creator = Depends(get_current_user),
) -> None:
    if not _is_admin(current_user) and current_user.id != creator_id:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Forbidden")

    deleted = service.delete_creator(creator_id)
    if not deleted:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Creator not found")
