from fastapi import FastAPI, HTTPException, Depends
from sqlalchemy.orm import Session
from fastapi.middleware.cors import CORSMiddleware

from models import User
from schemas import UserCreate, UserUpdate, UserResponse
from database import SessionLocal, engine, Base

app = FastAPI()

Base.metadata.create_all(bind=engine)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@app.post("/api/v1.0/users", response_model=UserResponse, status_code=201)
def create_user(user: UserCreate, db: Session = Depends(get_db)):
    if db.query(User).filter_by(login=user.login).first():
        raise HTTPException(status_code=403, detail="Login already exists")
    db_user = User(**user.dict())
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    return db_user

@app.get("/api/v1.0/users", response_model=list[UserResponse])
def get_users(db: Session = Depends(get_db)):
    return db.query(User).all()

@app.get("/api/v1.0/users/{user_id}", response_model=UserResponse)
def get_user(user_id: int, db: Session = Depends(get_db)):
    user = db.query(User).get(user_id)
    if not user:
        raise HTTPException(status_code=404)
    return user

@app.put("/api/v1.0/users", response_model=UserResponse)
def update_user(user: UserUpdate, db: Session = Depends(get_db)):
    db_user = db.query(User).get(user.id)
    if not db_user:
        raise HTTPException(status_code=404)
    for field, value in user.dict().items():
        setattr(db_user, field, value)
    db.commit()
    return db_user

@app.delete("/api/v1.0/users/{user_id}", status_code=204)
def delete_user(user_id: int, db: Session = Depends(get_db)):
    db_user = db.query(User).get(user_id)
    if not db_user:
        raise HTTPException(status_code=404)
    db.delete(db_user)
    db.commit()