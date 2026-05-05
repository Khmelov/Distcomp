from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from jose import jwt, JWTError
from sqlalchemy.orm import Session
from database import get_db
from core.security import SECRET_KEY, ALGORITHM
import models

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/api/v2.0/login")


async def get_current_user(token: str = Depends(oauth2_scheme), db: Session = Depends(get_db)):
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        login: str = payload.get("sub")
        if login is None:
            raise HTTPException(status_code=401, detail="Invalid token", headers={"errorCode": "40101"})
    except JWTError:
        raise HTTPException(status_code=401, detail="Invalid token", headers={"errorCode": "40101"})

    user = db.query(models.Author).filter(models.Author.login == login).first()
    if not user:
        raise HTTPException(status_code=401, detail="User not found")
    return user


def check_admin(user: models.Author = Depends(get_current_user)):
    if user.role != "ADMIN":
        raise HTTPException(status_code=403, detail="Forbidden", headers={"errorCode": "40301"})
    return user