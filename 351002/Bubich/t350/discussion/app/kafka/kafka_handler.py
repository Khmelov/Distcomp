import json
import threading
from kafka import KafkaConsumer, KafkaProducer
from kafka.errors import NoBrokersAvailable

# Стоп-слова для модерации
STOP_WORDS = ['spam', 'badword', 'xxx', 'offensive', 'buy now', 'click here']


class KafkaHandler:
    """Обработчик Kafka для discussion сервиса"""

    def __init__(self, comment_service):
        self.bootstrap_servers = 'localhost:9092'
        self.in_topic = 'InTopic'
        self.out_topic = 'OutTopic'
        self.comment_service = comment_service
        self.consumer = None
        self.producer = None

        try:
            self.producer = KafkaProducer(
                bootstrap_servers=self.bootstrap_servers,
                value_serializer=lambda v: json.dumps(v).encode('utf-8'),
                acks='all'
            )
            print("Discussion Kafka Producer connected")
        except NoBrokersAvailable:
            print("Warning: Kafka not available for discussion")
            self.producer = None

        if self.producer:
            self._start_consumer()

    def _start_consumer(self):
        """Запуск consumer для чтения из InTopic"""
        try:
            self.consumer = KafkaConsumer(
                self.in_topic,
                bootstrap_servers=self.bootstrap_servers,
                value_deserializer=lambda v: json.loads(v.decode('utf-8')),
                group_id='discussion-group',
                auto_offset_reset='earliest'
            )

            def consume():
                for message in self.consumer:
                    data = message.value
                    self._process_message(data)

            thread = threading.Thread(target=consume, daemon=True)
            thread.start()
            print("Discussion Kafka Consumer started")
        except Exception as e:
            print(f"Warning: Kafka Consumer error: {e}")
            self.consumer = None

    def _process_message(self, message: dict):
        """Обработка входящего сообщения"""
        request_id = message.get('requestId')
        action = message.get('action')
        data = message.get('data', {})

        try:
            if action == 'CREATE':
                comment = self.comment_service.create(data)
                # Модерация
                moderated_comment = self._moderate(comment)

                self._send_response(request_id, {
                    'success': True,
                    'comment': moderated_comment
                })

            elif action == 'READ':
                comment = self.comment_service.get_by_id(data.get('id'))
                self._send_response(request_id, {
                    'success': True,
                    'comment': comment
                })

            elif action == 'READ_ALL':
                comments = self.comment_service.get_by_story_id(data.get('storyId'))
                self._send_response(request_id, {
                    'success': True,
                    'comments': comments
                })

            elif action == 'UPDATE':
                comment = self.comment_service.update(data.get('id'), data)
                self._send_response(request_id, {
                    'success': True,
                    'comment': comment
                })

            elif action == 'DELETE':
                self.comment_service.delete(data.get('id'))
                self._send_response(request_id, {
                    'success': True
                })
        except Exception as e:
            self._send_response(request_id, {
                'success': False,
                'error': str(e)
            })

    def _moderate(self, comment: dict) -> dict:
        """
        Автоматическая модерация комментария.
        Проверяет наличие стоп-слов.
        """
        content = comment.get('content', '').lower()
        has_stop_words = any(word in content for word in STOP_WORDS)

        if has_stop_words:
            comment['state'] = 'DECLINED'
        else:
            comment['state'] = 'APPROVED'

        return comment

    def _send_response(self, request_id: str, response: dict):
        """Отправка ответа в OutTopic"""
        if not self.producer:
            return

        response['requestId'] = request_id

        try:
            # Используем request_id как ключ
            self.producer.send(
                self.out_topic,
                key=request_id.encode('utf-8'),
                value=response
            )
            self.producer.flush()
        except Exception as e:
            print(f"Error sending response: {e}")

    def close(self):
        if self.producer:
            self.producer.close()
        if self.consumer:
            self.consumer.close()