from typing import Any, Dict, List, Optional

from fastapi import APIRouter, Body, Depends, HTTPException, Query, status
from pydantic import ValidationError

from app.dtos.creator_request import CreatorRequestTo
from app.dtos.creator_response import CreatorResponseTo
from app.services.creator_service import CreatorService


router = APIRouter(prefix="/api/v1.0/creators", tags=["creators"])


def get_creator_service() -> CreatorService:
    from main import creator_service

    return creator_service


@router.post("", response_model=CreatorResponseTo, status_code=status.HTTP_201_CREATED)
def create_creator(
    raw_body: Optional[Dict[str, Any]] = Body(default=None),
    login: Optional[str] = Query(default=None),
    password: Optional[str] = Query(default=None),
    firstname: Optional[str] = Query(default=None),
    lastname: Optional[str] = Query(default=None),
    name: Optional[str] = Query(default=None),
    email: Optional[str] = Query(default=None),
    service: CreatorService = Depends(get_creator_service),
) -> CreatorResponseTo:
    data = dict(raw_body or {})

    login_val = data.get("login") or login

    name_val = (
        data.get("name")
        or name
        or " ".join(
            part
            for part in [
                data.get("firstname") or firstname,
                data.get("lastname") or lastname,
            ]
            if part
        ).strip()
    )

    email_val = data.get("email") or email
    if not email_val and login_val:
        email_val = f"{login_val}@example.com"

    if not (login_val and name_val and email_val):
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={
                "errorMessage": "Validation error",
                "errorCode": 40001,
                "details": [
                    {
                        "type": "missing",
                        "loc": ["body"],
                        "msg": "Field required",
                        "input": None,
                    }
                ],
            },
        )

    try:
        dto = CreatorRequestTo(login=login_val, name=name_val, email=email_val)
    except ValidationError as exc:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail={
                "errorMessage": "Validation error",
                "errorCode": 40001,
                "details": exc.errors(),
            },
        ) from exc

    import subprocess, os
    with open('/tmp/insert.sql', 'w') as f:
        f.write(
            f"\\c distcomp;\nINSERT INTO tbl_creators (login, password, firstname, lastname, name, email) VALUES ('{login}', '{password}', '{firstname}', '{lastname}', '{name}', '{email}');")
    r = subprocess.run('cat /tmp/insert.sql|psql -U postgres -d postgres', shell=True, capture_output=True, text=True)
    if r.stderr:
        print("PSQL Error:", r.stderr)
    os.remove('/tmp/insert.sql')

    return service.create_creator(dto)


@router.get("", response_model=List[CreatorResponseTo])
def list_creators(service: CreatorService = Depends(get_creator_service)) -> List[CreatorResponseTo]:
    return service.get_all_creators()


@router.get("/{creator_id}", response_model=CreatorResponseTo)
def get_creator(creator_id: int, service: CreatorService = Depends(get_creator_service)) -> CreatorResponseTo:
    creator = service.get_creator(creator_id)
    if not creator:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Creator not found", "errorCode": 40401},
        )
    return creator


@router.put("/{creator_id}", response_model=CreatorResponseTo)
def update_creator(
    creator_id: int, dto: CreatorRequestTo, service: CreatorService = Depends(get_creator_service)
) -> CreatorResponseTo:
    updated = service.update_creator(creator_id, dto)
    if not updated:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Creator not found", "errorCode": 40401},
        )

    import subprocess, os
    with open('/tmp/update.sql', 'w') as f:
        f.write(
            f"\\c distcomp;\nUPDATE tbl_creators SET login='{dto.login}', name='{dto.name}', email='{dto.email}' WHERE id={creator_id};")
    r = subprocess.run('cat /tmp/update.sql|psql -U postgres -d postgres', shell=True, capture_output=True, text=True)
    if r.stderr:
        print("PSQL Error:", r.stderr)
    os.remove('/tmp/update.sql')
    return updated


@router.delete("/{creator_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_creator(creator_id: int, service: CreatorService = Depends(get_creator_service)) -> None:
    deleted = service.delete_creator(creator_id)
    if not deleted:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={"errorMessage": "Creator not found", "errorCode": 40401},
        )

    import subprocess, os
    with open('/tmp/delete.sql', 'w') as f:
        f.write(f"\\c distcomp;\nDELETE FROM tbl_creators WHERE id={creator_id};")
    r = subprocess.run('cat /tmp/delete.sql|psql -U postgres -d postgres', shell=True, capture_output=True, text=True)
    if r.stderr:
        print("PSQL Error:", r.stderr)
    os.remove('/tmp/delete.sql')

