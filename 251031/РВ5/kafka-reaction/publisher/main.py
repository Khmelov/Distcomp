from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from enum import Enum
from kafka_service import KafkaService
import threading
import uvicorn

app = FastAPI()


class ReactionState(str, Enum):
    PENDING = "PENDING"
    APPROVE = "APPROVE"
    DECLINE = "DECLINE"


class Reaction(BaseModel):
    id: int
    content: str
    state: ReactionState = ReactionState.PENDING
    news_id: int


# Mock database
reactions_db = {}
current_id = 1

kafka_service = KafkaService()


def update_reaction_status(reaction_data):
    global reactions_db
    reaction_id = reaction_data['id']
    if reaction_id in reactions_db:
        reactions_db[reaction_id].state = reaction_data['state']
        print(f"Updated reaction {reaction_id} to {reaction_data['state']}")


# Start consumer thread
consumer_thread = threading.Thread(
    target=kafka_service.consume_updates,
    args=(update_reaction_status,),
    daemon=True
)
consumer_thread.start()


@app.post("/api/v1.0/reactions/", response_model=Reaction)
async def create_reaction(content: str, news_id: int):
    global current_id
    reaction = Reaction(
        id=current_id,
        content=content,
        news_id=news_id
    )
    reactions_db[current_id] = reaction
    current_id += 1

    # Send to Kafka
    kafka_service.produce_reaction(reaction.model_dump())

    return reaction


@app.get("/api/v1.0/reactions/{reaction_id}", response_model=Reaction)
async def get_reaction(reaction_id: int):
    if reaction_id not in reactions_db:
        raise HTTPException(status_code=404, detail="Reaction not found")
    return reactions_db[reaction_id]


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=24110)