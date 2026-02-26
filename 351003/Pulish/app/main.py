from fastapi import FastAPI
from app.api.v1 import users, topics, marks, comments
from app.core.error_handler import register_exception_handlers

app = FastAPI(title="Task310 REST API")

app.include_router(users.router, prefix="/api/v1.0")
app.include_router(topics.router, prefix="/api/v1.0")
app.include_router(marks.router, prefix="/api/v1.0")
app.include_router(comments.router, prefix="/api/v1.0")

register_exception_handlers(app)
