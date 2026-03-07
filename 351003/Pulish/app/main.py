from fastapi import FastAPI
from app.api.v1 import users, topics, marks, comments
from app.core.error_handler import register_exception_handlers
from app.db.database import Base, engine
from app.core.config import settings
from app.models import user, topic, mark, comment  # noqa

Base.metadata.create_all(bind=engine)

app = FastAPI(
    title=settings.APP_NAME,
    version=settings.APP_VERSION,
    debug=settings.DEBUG
)

app.include_router(users.router, prefix="/api/v1.0")
app.include_router(topics.router, prefix="/api/v1.0")
app.include_router(marks.router, prefix="/api/v1.0")
app.include_router(comments.router, prefix="/api/v1.0")

register_exception_handlers(app)


@app.get("/")
async def root():
    return {
        "message": settings.APP_NAME,
        "version": settings.APP_VERSION,
        "database": settings.POSTGRES_DB,
        "debug": settings.DEBUG
    }


@app.get("/health")
async def health_check():
    return {"status": "healthy"}
