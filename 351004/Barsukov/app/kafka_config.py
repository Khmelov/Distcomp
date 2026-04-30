import os

KAFKA_BOOTSTRAP_SERVERS = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
IN_TOPIC = "InTopic"
OUT_TOPIC = "OutTopic"
CONSUMER_GROUP_PUBLISHER = "publisher-group"
CONSUMER_GROUP_DISCUSSION = "discussion-group"