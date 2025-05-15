from fastapi import FastAPI, HTTPException
from confluent_kafka import Producer, Consumer
import json
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from models import Base, Reaction
from schemas import ReactionCreate, Reaction, ReactionState
from config import Config
import threading
import logging
import uuid

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
    'group.id': 'publisher-group',
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


# Function to send reaction to Kafka
def send_reaction_to_kafka(reaction: Reaction):
    try:
        reaction_data = {
            "id": reaction.id,
            "content": reaction.content,
            "state": reaction.state,
            "news_id": reaction.news_id
        }

        # Ensure messages for the same news go to the same partition
        producer.produce(
            Config.IN_TOPIC,
            key=str(reaction.news_id),
            value=json.dumps(reaction_data),
            callback=delivery_report
        )
        producer.flush()
        logger.info(f"Sent reaction {reaction.id} to Kafka topic {Config.IN_TOPIC}")
    except Exception as e:
        logger.error(f"Error sending reaction to Kafka: {e}")


# Kafka consumer thread
def consume_reaction_updates():
    consumer = Consumer(consumer_conf)
    consumer.subscribe([Config.OUT_TOPIC])

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

                reaction = db.query(Reaction).filter(Reaction.id == reaction_data["id"]).first()
                if reaction:
                    reaction.state = reaction_data["state"]
                    db.commit()
                    logger.info(f"Updated reaction {reaction.id} with state {reaction.state}")
                else:
                    logger.warning(f"Reaction {reaction_data['id']} not found in database")

                db.close()
            except Exception as e:
                logger.error(f"Error processing message: {e}")
    finally:
        consumer.close()


# Start consumer thread
consumer_thread = threading.Thread(target=consume_reaction_updates, daemon=True)
consumer_thread.start()


@app.on_event("startup")
async def startup_event():
    logger.info("Starting up publisher service...")


@app.post("/api/v1.0/reactions/", response_model=Reaction)
async def create_reaction(reaction: ReactionCreate):
    db = SessionLocal()
    try:
        db_reaction = Reaction(**reaction.model_dump())
        db.add(db_reaction)
        db.commit()
        db.refresh(db_reaction)

        # Send to Kafka
        send_reaction_to_kafka(db_reaction)

        return db_reaction
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=400, detail=str(e))
    finally:
        db.close()


@app.get("/api/v1.0/reactions/{reaction_id}", response_model=Reaction)
async def read_reaction(reaction_id: int):
    db = SessionLocal()
    reaction = db.query(Reaction).filter(Reaction.id == reaction_id).first()
    db.close()
    if reaction is None:
        raise HTTPException(status_code=404, detail="Reaction not found")
    return reaction


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=Config.APP_PORT)