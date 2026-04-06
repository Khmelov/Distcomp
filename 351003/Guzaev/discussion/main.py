import uvicorn
from fastapi import FastAPI
from controllers.comment_controller import router
from repositories.comment_repository import CommentRepository
from services.kafka_service import start_kafka_thread

app = FastAPI()

@app.on_event("startup")
def startup():
    repo = CommentRepository()
    start_kafka_thread(repo)

app.include_router(router)

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=24130)