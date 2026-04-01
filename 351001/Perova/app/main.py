import uvicorn
from fastapi import FastAPI

from app.exceptions import register_exception_handlers
from app.routers import issue_router, notice_router, sticker_router, user_router

app = FastAPI(title="Task310 REST API", version="1.0")

app.include_router(user_router)
app.include_router(issue_router)
app.include_router(sticker_router)
app.include_router(notice_router)

register_exception_handlers(app)


if __name__ == "__main__":
    uvicorn.run("app.main:app", host="localhost", port=24110, reload=False)
