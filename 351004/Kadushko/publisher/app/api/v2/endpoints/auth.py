from fastapi import APIRouter, Depends
from fastapi.responses import JSONResponse
from sqlalchemy.orm import Session
from app.database import get_db
from app.schemas.editor import EditorCreate, EditorResponse, LoginRequest, TokenResponse
from app.services.editor_service import EditorService
from app.auth import verify_password, create_token
from app.core.exceptions import EntityNotFoundException

router = APIRouter(tags=["auth-v2"])


@router.post("/editors", response_model=EditorResponse, status_code=201)
def register(data: EditorCreate, db: Session = Depends(get_db)):
    result = EditorService(db).create(data)
    return JSONResponse(status_code=201, content=result.model_dump())


@router.post("/login", response_model=TokenResponse)
def login(data: LoginRequest, db: Session = Depends(get_db)):
    svc = EditorService(db)
    editor = svc.get_by_login(data.login)
    if not editor or not verify_password(data.password, editor.password):
        return JSONResponse(
            status_code=401,
            content={"errorMessage": "Invalid login or password", "errorCode": 40101}
        )
    token = create_token(editor.login, editor.role)
    return JSONResponse(content={"access_token": token, "token_type": "Bearer"})
