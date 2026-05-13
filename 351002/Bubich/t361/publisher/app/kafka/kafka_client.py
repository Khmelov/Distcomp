import json
import threading
import uuid
from kafka import KafkaProducer, KafkaConsumer
from kafka.errors import NoBrokersAvailable


class KafkaClient:
    """Kafka клиент для publisher"""

    def __init__(self):
        self.bootstrap_servers = 'localhost:9092'
        self.in_topic = 'InTopic'
        self.out_topic = 'OutTopic'
        self.producer = None
        self.consumer = None
        self.pending_responses = {}  # request_id -> response
        self._lock = threading.Lock()

        try:
            self.producer = KafkaProducer(
                bootstrap_servers=self.bootstrap_servers,
                value_serializer=lambda v: json.dumps(v).encode('utf-8'),
                key_serializer=lambda k: str(k).encode('utf-8') if k else None,
                acks='all',
                retries=3
            )
            print("Kafka Producer connected")
        except NoBrokersAvailable:
            print("Warning: Kafka not available. Using direct REST calls.")
            self.producer = None

        if self.producer:
            self._start_consumer()

    def _start_consumer(self):
        """Запуск consumer для чтения ответов из OutTopic"""
        try:
            self.consumer = KafkaConsumer(
                self.out_topic,
                bootstrap_servers=self.bootstrap_servers,
                value_deserializer=lambda v: json.loads(v.decode('utf-8')),
                group_id='publisher-group',
                auto_offset_reset='earliest'
            )

            def consume():
                for message in self.consumer:
                    data = message.value
                    request_id = data.get('requestId')
                    with self._lock:
                        self.pending_responses[request_id] = data

            thread = threading.Thread(target=consume, daemon=True)
            thread.start()
            print("Kafka Consumer started")
        except Exception as e:
            print(f"Warning: Kafka Consumer error: {e}")
            self.consumer = None

    def send_request(self, action: str, data: dict, story_id: int = None) -> dict:
        """
        Отправка запроса через Kafka.

        Args:
            action: 'CREATE', 'READ', 'UPDATE', 'DELETE', 'READ_ALL'
            data: данные запроса
            story_id: ID story для партиционирования (все комментарии одной story в одной партиции)
        """
        if not self.producer:
            return None

        request_id = str(uuid.uuid4())
        message = {
            'requestId': request_id,
            'action': action,
            'data': data
        }

        # Используем story_id как ключ для партиционирования
        # Это гарантирует, что все сообщения одной story попадут в одну партицию
        key = str(story_id) if story_id else request_id

        try:
            self.producer.send(
                self.in_topic,
                key=key,
                value=message
            )
            self.producer.flush()

            # Ждем ответ с таймаутом
            import time
            timeout = 5  # секунд
            start_time = time.time()

            while time.time() - start_time < timeout:
                with self._lock:
                    if request_id in self.pending_responses:
                        response = self.pending_responses.pop(request_id)
                        return response
                time.sleep(0.1)

            return None  # Таймаут
        except Exception as e:
            print(f"Kafka send error: {e}")
            return None

    def close(self):
        if self.producer:
            self.producer.close()
        if self.consumer:
            self.consumer.close()