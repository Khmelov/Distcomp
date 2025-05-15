from fastapi import FastAPI, HTTPException
from confluent_kafka import Producer, Consumer
import json
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from models import Base, Reaction
from schemas import Reaction, ReactionUpdate, ReactionState
from config import Config
import threading
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Database setup
engine = create_engine(Config.DB_URL)
Base.metadata.create_all(bind=engine)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Kafka setup
producer_conf = {
    'bootstrap.servers': Config.KAFKA_BOOTSTRAP_SERVERS
}

consumer_conf = {
    'bootstrap.servers': Config.KAFKA_BOOTSTRAP_SERVERS,
    'group.id': 'discussion-group',
    'auto.offset.reset': 'earliest'
}

app = FastAPI()


# Kafka producer callback
def delivery_report(err, msg):
    if err is not None:
        logger.error(f'Message delivery failed: {err}')
    else:
        logger.info(f'Message delivered to {msg.topic()} [{msg.partition()}]')


# Kafka producer
producer = Producer(producer_conf)


# Moderation function
def moderate_reaction(content: str) -> ReactionState:
    content_lower = content.lower()
    for word in Config.STOP_WORDS:
        if word in content_lower:
            return ReactionState.DECLINE
    return ReactionState.APPROVE


# Function to send reaction update to Kafka
def send_reaction_update_to_kafka(reaction: Reaction):
    try:
        reaction_data = {
            "id": reaction.id,
            "content": reaction.content,
            "state": reaction.state,
            "news_id": reaction.news_id
        }

        producer.produce(
            Config.OUT_TOPIC,
            key=str(reaction.news_id),
            value=json.dumps(reaction_data),
            callback=delivery_report
        )
        producer.flush()
        logger.info(f"Sent reaction update {reaction.id} to Kafka topic {Config.OUT_TOPIC}")
    except Exception as e:
        logger.error(f"Error sending reaction update to Kafka: {e}")


# Kafka consumer thread
def consume_reactions():
    consumer = Consumer(consumer_conf)
    consumer.subscribe([Config.IN_TOPIC])

    try:
        while True:
            msg = consumer.poll(1.0)
            if msg is None:
                continue
            if msg.error():
                logger.error(f"Consumer error: {msg.error()}")
                continue

            try:
                reaction_data = json.loads(msg.value().decode('utf-8'))
                db = SessionLocal()

                # Create reaction in discussion database
                reaction = Reaction(
                    id=reaction_data["id"],
                    content=reaction_data["content"],
                    state=ReactionState.PENDING,
                    news_id=reaction_data["news_id"]
                )
                db.add(reaction)
                db.commit()

                # Moderate reaction
                new_state = moderate_reaction(reaction.content)
                reaction.state = new_state
                db.commit()

                # Send update back to publisher
                send_reaction_update_to_kafka(reaction)

                logger.info(f"Processed reaction {reaction.id} with state {reaction.state}")
                db.close()
            except Exception as e:
                logger.error(f"Error processing message: {e}")
                if 'db' in locals():
                    db.rollback()
                    db.close()
    finally:
        consumer.close()


# Start consumer thread
consumer_thread = threading.Thread(target=consume_reactions, daemon=True)
consumer_thread.start()


@app.on_event("startup")
async def startup_event():
    logger.info("Starting up discussion service...")


@app.get("/api/v1.0/reactions/{reaction_id}", response_model=Reaction)
async def read_reaction(reaction_id: int):
    db = SessionLocal()
    reaction = db.query(Reaction).filter(Reaction.id == reaction_id).first()
    db.close()
    if reaction is None:
        raise HTTPException(status_code=404, detail="Reaction not found")
    return reaction


@app.put("/api/v1.0/reactions/{reaction_id}", response_model=Reaction)
async def update_reaction(reaction_id: int, reaction_update: ReactionUpdate):
    db = SessionLocal()
    try:
        reaction = db.query(Reaction).filter(Reaction.id == reaction_id).first()
        if reaction is None:
            raise HTTPException(status_code=404, detail="Reaction not found")

        reaction.state = reaction_update.state
        db.commit()
        db.refresh(reaction)

        # Send update to publisher
        send_reaction_update_to_kafka(reaction)

        return reaction
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=400, detail=str(e))
    finally:
        db.close()


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=Config.APP_PORT)