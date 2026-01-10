import json
import logging
import threading
import time
import sys
import os
from typing import Dict, Any, Optional

sys.path.append(os.path.join(os.path.dirname(__file__), '..', '..', '..'))

from shared.kafka_service import KafkaService

class PublisherKafkaService:
    def __init__(self):
        self.kafka_service = KafkaService(bootstrap_servers='localhost:9092')
        self.in_topic = "InTopic"
        self.out_topic = "OutTopic"
        self.pending_requests: Dict[str, Optional[Dict[str, Any]]] = {}
        self._orphan_responses: Dict[str, Dict[str, Any]] = {}
        self._orphan_timestamps: Dict[str, float] = {}
        self._lock = threading.Lock()
        self._cv = threading.Condition(self._lock)
        self.consumer = None
        self.consumer_thread = None
        self._start_consumer()
    
    def _start_consumer(self):
        try:
            self.consumer = self.kafka_service.create_consumer(
                topic=self.out_topic,
                group_id="publisher_response_group"
            )
            
            logging.info(f"Publisher consumer created for topic {self.out_topic}")
            
            self.consumer_thread = threading.Thread(
                target=self._consume_responses,
                daemon=True
            )
            self.consumer_thread.start()
            logging.info("Publisher Kafka consumer started")
            
        except Exception as e:
            logging.error(f"Failed to start publisher consumer: {e}")
            raise
    
    def _consume_responses(self):
        try:
            for message in self.consumer:
                try:
                    logging.info(f"Received raw message: {message.value}")
                    response = message.value
                    
                    logging.info(f"Parsed response: {response}")
                    request_id = response.get('request_id')

                    if not request_id:
                        logging.warning("Received response without request_id")
                        continue

                    with self._cv:
                        if request_id in self.pending_requests:
                            self.pending_requests[request_id] = response
                            logging.info(f"Received response for request {request_id}")
                        else:
                            # The REST thread might have timed out and removed the entry.
                            # Keep the response for a short time so a late waiter can still pick it up.
                            self._orphan_responses[request_id] = response
                            self._orphan_timestamps[request_id] = time.time()
                            logging.warning(f"Received response for unknown request {request_id}")

                        self._cv.notify_all()
                    
                except Exception as e:
                    logging.error(f"Error processing response message: {e}")
                    
        except Exception as e:
            logging.error(f"Consumer error: {e}")

    def _cleanup_orphans(self, ttl_seconds: float = 30.0) -> None:
        now = time.time()
        expired = [rid for rid, ts in self._orphan_timestamps.items() if now - ts > ttl_seconds]
        for rid in expired:
            self._orphan_timestamps.pop(rid, None)
            self._orphan_responses.pop(rid, None)
    
    def send_note_request(self, note_data: Dict[str, Any], request_id: str) -> bool:
        try:
            message = {
                'request_id': request_id,
                'action': 'create_note' if 'id' not in note_data else 'update_note',
                'data': note_data,
                'timestamp': time.time()
            }
            
            issue_id = note_data.get('issueId', 0)
            
            success = self.kafka_service.send_message(
                topic=self.in_topic,
                message=message,
                key=str(issue_id)
            )
            
            if success:
                with self._cv:
                    self.pending_requests[request_id] = None
                    self._cleanup_orphans()
                logging.info(f"Sent note request {request_id} for issue {issue_id}")
            
            return success
            
        except Exception as e:
            logging.error(f"Error sending note request: {e}")
            return False
    
    def wait_for_response(self, request_id: str, timeout: float = 5.0) -> Optional[Dict[str, Any]]:
        deadline = time.time() + timeout

        with self._cv:
            # Response might have arrived before we started waiting
            self._cleanup_orphans()
            if request_id in self._orphan_responses:
                response = self._orphan_responses.pop(request_id, None)
                self._orphan_timestamps.pop(request_id, None)
                return response

            # Ensure there is a pending slot
            self.pending_requests.setdefault(request_id, None)

            while True:
                response = self.pending_requests.get(request_id)
                if response is not None:
                    self.pending_requests.pop(request_id, None)
                    return response

                # Also check late responses
                if request_id in self._orphan_responses:
                    response = self._orphan_responses.pop(request_id, None)
                    self._orphan_timestamps.pop(request_id, None)
                    self.pending_requests.pop(request_id, None)
                    return response

                remaining = deadline - time.time()
                if remaining <= 0:
                    break

                self._cv.wait(timeout=min(0.25, remaining))

            # Do not immediately delete the pending slot: late response may still arrive.
            # Keep it for a short window and cleanup via orphan TTL.
            logging.warning(f"Timeout waiting for response to request {request_id}")
            return None
    
    def close(self):
        if self.consumer:
            self.consumer.close()
        
        self.kafka_service.close()
        logging.info("Publisher Kafka service closed")
