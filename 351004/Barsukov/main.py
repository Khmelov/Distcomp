import uvicorn
from fastapi import FastAPI
from app.api.v1.router import api_router

app = FastAPI()

app.include_router(api_router, prefix="/api/v1.0")

if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=24110)
