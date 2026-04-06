from fastapi import FastAPI
import uvicorn
from app.api.endpoints.writers import router as writers_router
from app.api.endpoints.articles import router as articles_router
from app.api.endpoints.markers import router as markers_router
from app.api.endpoints.notes import router as notes_router
from app.core.exceptions import AppError, app_error_handler

app = FastAPI()
app.add_exception_handler(AppError, app_error_handler)
app.include_router(writers_router)
app.include_router(articles_router)
app.include_router(markers_router)
app.include_router(notes_router)


if __name__ == "__main__":
    uvicorn.run("main:app", reload = True, host = "0.0.0.0", port = 24110, http = "h11")

