import json
import logging
from typing import Dict, Any, Optional
from kafka import KafkaProducer, KafkaConsumer
from kafka.errors import KafkaError
import threading
import time

class KafkaService:
    def __init__(self, bootstrap_servers: str = 'localhost:9092'):
        self.bootstrap_servers = bootstrap_servers
        self.producer = None
        self.consumer = None
        self._connect_producer()
    
    def _connect_producer(self):
        try:
            self.producer = KafkaProducer(
                bootstrap_servers=self.bootstrap_servers,
                value_serializer=lambda v: json.dumps(v, default=str).encode('utf-8'),
                key_serializer=lambda k: str(k).encode('utf-8') if k else None,
                acks='all',
                retries=3,
                batch_size=16384,
                linger_ms=10,
                buffer_memory=33554432
            )
            logging.info("Kafka producer connected successfully")
        except Exception as e:
            logging.error(f"Failed to connect Kafka producer: {e}")
            raise
    
    def send_message(self, topic: str, message: Dict[str, Any], key: Optional[str] = None, partition: Optional[int] = None) -> bool:
        try:
            future = self.producer.send(
                topic=topic,
                value=message,
                key=key,
                partition=partition
            )
            
            record_metadata = future.get(timeout=10)
            
            logging.info(f"Message sent to topic {topic}, partition {record_metadata.partition}, offset {record_metadata.offset}")
            return True
            
        except KafkaError as e:
            logging.error(f"Failed to send message to topic {topic}: {e}")
            return False
        except Exception as e:
            logging.error(f"Unexpected error sending message to topic {topic}: {e}")
            return False
    
    def create_consumer(self, topic: str, group_id: str) -> KafkaConsumer:
        try:
            consumer = KafkaConsumer(
                topic,
                bootstrap_servers=self.bootstrap_servers,
                group_id=group_id,
                value_deserializer=lambda m: json.loads(m.decode('utf-8')),
                key_deserializer=lambda k: k.decode('utf-8') if k else None,
                auto_offset_reset='earliest',
                enable_auto_commit=True,
                session_timeout_ms=30000,
                heartbeat_interval_ms=3000
            )
            logging.info(f"Kafka consumer created for topic {topic}, group {group_id}")
            return consumer
            
        except Exception as e:
            logging.error(f"Failed to create Kafka consumer for topic {topic}: {e}")
            raise
    
    def close(self):
        if self.producer:
            self.producer.flush()
            self.producer.close()
            logging.info("Kafka producer closed")
        
        if self.consumer:
            self.consumer.close()
            logging.info("Kafka consumer closed")
