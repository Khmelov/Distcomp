from typing import Optional
from fastapi import APIRouter, HTTPException, Depends
from pydantic import BaseModel
from repositories.writer_repository import WriterRepository
from security.jwt_service import create_access_token, verify_password, hash_password
from security.dependencies import require_auth, require_admin

router = APIRouter(prefix="/api/v2.0")


class WriterRegisterRequest(BaseModel):
    login: str
    password: str
    firstName: str
    lastName: str
    role: Optional[str] = "CUSTOMER"


class LoginRequest(BaseModel):
    login: str
    password: str


@router.post("/writers", status_code=201)
def register_writer(body: WriterRegisterRequest):
    repo = WriterRepository()
    if repo.get_by_login(body.login):
        raise HTTPException(status_code=403, detail={"errorMessage": "Login already exists", "errorCode": 40300})
    hashed = hash_password(body.password)
    writer = repo.create(
        login=body.login,
        password=hashed,
        firstname=body.firstName,
        lastname=body.lastName,
        role=body.role.upper()
    )
    return {
        "id": writer.id,
        "login": writer.login,
        "firstName": writer.firstname,
        "lastName": writer.lastname,
        "role": writer.role
    }


@router.post("/login", status_code=200)
def login(body: LoginRequest):
    repo = WriterRepository()
    writer = repo.get_by_login(body.login)
    if not writer or not verify_password(body.password, writer.password):
        raise HTTPException(status_code=401, detail={"errorMessage": "Invalid credentials", "errorCode": 40100})
    token = create_token(writer.login, writer.role)
    return {"access_token": token, "token_type": "bearer"}


@router.get("/writers", status_code=200)
def get_writers(user=Depends(require_auth)):
    writers = WriterRepository().get_all()
    return [
        {"id": w.id, "login": w.login, "firstName": w.firstname,
         "lastName": w.lastname, "role": w.role}
        for w in writers
    ]


@router.get("/writers/{writer_id}", status_code=200)
def get_writer(writer_id: int, user=Depends(require_auth)):
    w = WriterRepository().get_by_id(writer_id)
    if not w:
        raise HTTPException(status_code=404, detail={"errorMessage": "Writer not found", "errorCode": 40400})
    return {"id": w.id, "login": w.login, "firstName": w.firstname,
            "lastName": w.lastname, "role": w.role}


@router.delete("/writers/{writer_id}", status_code=204)
def delete_writer(writer_id: int, user=Depends(require_admin)):
    ok = WriterRepository().delete(writer_id)
    if not ok:
        raise HTTPException(status_code=404, detail={"errorMessage": "Not found", "errorCode": 40400})


@router.get("/comments", status_code=200)
def get_comments(user=Depends(require_auth)):
    from services.kafka_comment_service import send_and_wait
    result = send_and_wait("GET_ALL", {})
    if result is None:
        return []
    if isinstance(result, dict) and "errorCode" in result:
        raise HTTPException(status_code=500, detail=result)
    return result


@router.post("/comments", status_code=201)
def create_comment(body: dict, user=Depends(require_auth)):
    from services.kafka_comment_service import send_and_wait
    import random
    data = {
        **body,
        "id": random.randint(1, 10**9),
        "country": body.get("country", "Belarus"),
        "tweetId": body.get("tweetId")
    }
    result = send_and_wait("CREATE", data, tweet_id=body.get("tweetId"))
    if result is None or (isinstance(result, dict) and "errorCode" in result):
        raise HTTPException(status_code=400, detail=result or {"errorMessage": "Timeout", "errorCode": 40001})
    return result


@router.get("/comments/{comment_id}", status_code=200)
def get_comment(comment_id: int, user=Depends(require_auth)):
    from services.kafka_comment_service import send_and_wait
    result = send_and_wait("GET", {"id": comment_id})
    if result is None or (isinstance(result, dict) and "errorCode" in result):
        raise HTTPException(status_code=404, detail=result or {"errorMessage": "Not found", "errorCode": 40400})
    return result


@router.delete("/comments/{comment_id}", status_code=204)
def delete_comment(comment_id: int, user=Depends(require_auth)):
    from services.kafka_comment_service import send_and_wait
    result = send_and_wait("DELETE", {"id": comment_id})
    if result is None or (isinstance(result, dict) and "errorCode" in result):
        raise HTTPException(status_code=404, detail=result or {"errorMessage": "Not found", "errorCode": 40400})