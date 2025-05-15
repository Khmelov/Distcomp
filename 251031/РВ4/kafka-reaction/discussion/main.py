from fastapi import FastAPI
from kafka_service import KafkaService
import threading
import uvicorn

app = FastAPI()

kafka_service = KafkaService()

# Start processing thread
processing_thread = threading.Thread(
    target=kafka_service.process_reactions,
    daemon=True
)
processing_thread.start()

@app.get("/api/v1.0/health")
async def health_check():
    return {"status": "OK"}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=24130)