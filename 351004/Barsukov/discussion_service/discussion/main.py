import uvicorn
from fastapi import FastAPI
from contextlib import asynccontextmanager
from discussion.api.v1.router import api_router
from discussion.db.thecassandra import cassandra_client
from discussion.core.config import settings

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    print("Starting discussion service...")
    cassandra_client.connect()
    print(f"Connected to Cassandra at {settings.CASSANDRA_HOSTS}:{settings.CASSANDRA_PORT}")
    yield
    # Shutdown
    cassandra_client.close()
    print("Discussion service stopped")

app = FastAPI(
    title="Discussion Service",
    description="Microservice for managing notes with Cassandra",
    version="1.0.0",
    lifespan=lifespan
)

app.include_router(api_router)

if __name__ == "__main__":
    uvicorn.run(
        "discussion.main:app",
        host="127.0.0.1",
        port=24130,
        reload=True
    )