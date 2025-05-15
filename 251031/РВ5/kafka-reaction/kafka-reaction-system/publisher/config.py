import os
from dotenv import load_dotenv

load_dotenv()

class Config:
    KAFKA_BOOTSTRAP_SERVERS = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
    IN_TOPIC = "ReactionInTopic"
    OUT_TOPIC = "ReactionOutTopic"
    APP_PORT = 24110
    DB_URL = "sqlite:///./test.db"