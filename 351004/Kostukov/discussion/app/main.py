from contextlib import asynccontextmanager

from fastapi import FastAPI
import uvicorn

from discussion.app.infrastructure.cassandra.repo import CassandraNoteRepository
from discussion.app.infrastructure.cassandra.session import get_cassandra_session, shutdown_cassandra
from discussion.app.router import router as note_router
from discussion.app.services.kafka_worker import NoteKafkaWorker
from discussion.app.services.note_service import NoteService


@asynccontextmanager
async def lifespan(app: FastAPI):
    session = get_cassandra_session()
    repo = CassandraNoteRepository(session=session, bucket_count=16)

    app.state.note_service = NoteService(repo)
    app.state.note_kafka_worker = NoteKafkaWorker(repo)

    await app.state.note_kafka_worker.start()
    try:
        yield
    finally:
        await app.state.note_kafka_worker.stop()
        shutdown_cassandra()


app = FastAPI(title="discussion", lifespan=lifespan)
app.include_router(note_router)


if __name__ == "__main__":
    uvicorn.run("main:app", reload=True, host="0.0.0.0", port=24130, http="h11")