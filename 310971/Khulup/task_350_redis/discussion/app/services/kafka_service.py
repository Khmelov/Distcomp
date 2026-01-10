import json
import logging
import threading
import time
import sys
import os
from typing import Dict, Any, List

sys.path.append(os.path.join(os.path.dirname(__file__), '..', '..', '..'))

from shared.kafka_service import KafkaService
from app.services.note_service import NoteService
from app.repositories.note_repository import NoteRepository

class DiscussionKafkaService:
    def __init__(self):
        self.kafka_service = KafkaService(bootstrap_servers='localhost:9092')
        self.in_topic = "InTopic"
        self.out_topic = "OutTopic"
        self.note_service = None
        self.consumer = None
        self.consumer_thread = None
        self._initialize_services()
        self._start_consumer()
    
    def _initialize_services(self):
        try:
            repository = NoteRepository()
            self.note_service = NoteService(repository)
            logging.info("Discussion services initialized")
        except Exception as e:
            logging.error(f"Failed to initialize discussion services: {e}")
            raise
    
    def _start_consumer(self):
        try:
            self.consumer = self.kafka_service.create_consumer(
                topic=self.in_topic,
                group_id="discussion_request_group"
            )
            
            self.consumer_thread = threading.Thread(
                target=self._consume_requests,
                daemon=True
            )
            self.consumer_thread.start()
            logging.info("Discussion Kafka consumer started")
            
        except Exception as e:
            logging.error(f"Failed to start discussion consumer: {e}")
            raise
    
    def _consume_requests(self):
        try:
            for message in self.consumer:
                try:
                    request = message.value
                    self._process_request(request)
                    
                except Exception as e:
                    logging.error(f"Error processing request message: {e}")
                    
        except Exception as e:
            logging.error(f"Consumer error: {e}")
    
    def _process_request(self, request: Dict[str, Any]):
        try:
            request_id = request.get('request_id')
            action = request.get('action')
            data = request.get('data')
            
            logging.info(f"Processing {action} request {request_id}")
            
            if action == 'create_note':
                response = self._handle_create_note(request_id, data)
            elif action == 'update_note':
                response = self._handle_update_note(request_id, data)
            else:
                response = {
                    'request_id': request_id,
                    'success': False,
                    'message': f'Unknown action: {action}',
                    'data': None
                }
            
            self._send_response(response)
            
        except Exception as e:
            logging.error(f"Error processing request: {e}")
            error_response = {
                'request_id': request.get('request_id'),
                'success': False,
                'message': str(e),
                'data': None
            }
            self._send_response(error_response)
    
    def _handle_create_note(self, request_id: str, data: Dict[str, Any]) -> Dict[str, Any]:
        try:
            from app.schemas.note import NoteCreate
            
            note_create = NoteCreate(
                issueId=data.get('issueId'),
                content=data.get('content')
            )
            
            note = self.note_service.create_note(note_create)
            
            note_read_obj = self.note_service.note_to_read_schema(note)
            note_dict = note_read_obj.model_dump() if hasattr(note_read_obj, 'model_dump') else note_read_obj.dict()
            
            return {
                'request_id': request_id,
                'success': True,
                'message': 'Note created successfully',
                'data': note_dict
            }
            
        except Exception as e:
            logging.error(f"Error creating note: {e}")
            return {
                'request_id': request_id,
                'success': False,
                'message': str(e),
                'data': None
            }
    
    def _handle_update_note(self, request_id: str, data: Dict[str, Any]) -> Dict[str, Any]:
        try:
            from app.schemas.note import NoteUpdate
            
            note_update = NoteUpdate(
                id=data.get('id'),
                issueId=data.get('issueId'),
                content=data.get('content')
            )
            
            note = self.note_service.update_note(note_update)
            
            if note:
                note_read_obj = self.note_service.note_to_read_schema(note)
                note_dict = note_read_obj.model_dump() if hasattr(note_read_obj, 'model_dump') else note_read_obj.dict()
                
                return {
                    'request_id': request_id,
                    'success': True,
                    'message': 'Note updated successfully',
                    'data': note_dict
                }
            else:
                return {
                    'request_id': request_id,
                    'success': False,
                    'message': 'Note not found',
                    'data': None
                }
                
        except Exception as e:
            logging.error(f"Error updating note: {e}")
            return {
                'request_id': request_id,
                'success': False,
                'message': str(e),
                'data': None
            }
    
    def _send_response(self, response: Dict[str, Any]):
        try:
            logging.info(f"Sending response: {response}")
            success = self.kafka_service.send_message(
                topic=self.out_topic,
                message=response
            )
            
            if success:
                logging.info(f"Response sent for request {response.get('request_id')}")
            else:
                logging.error(f"Failed to send response for request {response.get('request_id')}")
                
        except Exception as e:
            logging.error(f"Error sending response: {e}")
    
    def close(self):
        if self.consumer:
            self.consumer.close()
        
        self.kafka_service.close()
        logging.info("Discussion Kafka service closed")
