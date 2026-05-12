import os
import sys
import json
from django.core.management.base import BaseCommand
from confluent_kafka import Consumer, Producer

# Исправление путей для импорта сессии
sys.path.append(os.path.join(os.path.dirname(__file__), '../../..'))

try:
    from api.views import session
except ImportError:
    session = None

class Command(BaseCommand):
    help = 'Kafka consumer for Discussion service'

    def handle(self, *args, **options):
        conf = {
            'bootstrap.servers': 'localhost:9092',
            'group.id': 'discussion-backend-group',
            'auto.offset.reset': 'earliest'
        }
        consumer = Consumer(conf)
        producer = Producer({'bootstrap.servers': 'localhost:9092'})
        consumer.subscribe(['InTopic'])

        self.stdout.write(self.style.SUCCESS('Discussion service успешно запущен и слушает InTopic...'))

        try:
            while True:
                msg = consumer.poll(1.0)
                if msg is None: continue
                if msg.error(): continue

                req = json.loads(msg.value().decode('utf-8'))
                action = req.get('action')
                data = req.get('data')
                corr_id = req.get('correlation_id')

                result = {}
                status = 200

                try:
                    # 1. Обработка POST (Асинхронно, ответ не ждем)
                    if action == 'POST':
                        query = "INSERT INTO tbl_message (country, issue_id, id, content) VALUES (%s, %s, %s, %s)"
                        session.execute(query, (
                            data.get('country', 'BY'),
                            int(data['issueId']),
                            int(data['id']),
                            data['content']
                        ))
                        self.stdout.write(f"Успешный POST: id {data['id']}")

                    # 2. Обработка GET
                    elif action == 'GET':
                        row = session.execute("SELECT * FROM tbl_message WHERE id=%s ALLOW FILTERING", [int(data['id'])]).one()
                        if row:
                            result = {"id": row.id, "issueId": row.issue_id, "country": row.country, "content": row.content}
                        else:
                            result = {"detail": "Not found"}
                            status = 404

                    # 3. Обработка PUT
                    elif action == 'PUT':
                        row = session.execute("SELECT * FROM tbl_message WHERE id=%s ALLOW FILTERING",
                                              [int(data['id'])]).one()
                        if row:
                            query = "UPDATE tbl_message SET content=%s WHERE country=%s AND issue_id=%s AND id=%s"
                            session.execute(query, (data['content'], row.country, row.issue_id, row.id))
                            # Возвращаем полный объект, включая issueId
                            result = {
                                "id": row.id,
                                "issueId": row.issue_id,
                                "content": data['content'],
                                "status": "updated"
                            }
                        else:
                            result = {"detail": "Not found"}
                            status = 404

                    # 4. Обработка DELETE
                    elif action == 'DELETE':
                        row = session.execute("SELECT * FROM tbl_message WHERE id=%s ALLOW FILTERING", [int(data['id'])]).one()
                        if row:
                            query = "DELETE FROM tbl_message WHERE country=%s AND issue_id=%s AND id=%s"
                            session.execute(query, (row.country, row.issue_id, row.id))
                            result = {"detail": "Deleted"}
                        else:
                            result = {"detail": "Not found"}
                            status = 404

                except Exception as e:
                    self.stdout.write(self.style.ERROR(f"Database error: {e}"))
                    result = {"detail": str(e)}
                    status = 500

                # Отправка ответа в OutTopic (только если есть corr_id)
                if corr_id:
                    response = {
                        'correlation_id': corr_id,
                        'result': result,
                        'status': status
                    }
                    producer.produce('OutTopic', json.dumps(response).encode('utf-8'))
                    producer.flush()
                    self.stdout.write(f"Отправлен ответ: {action} | Status: {status}")

        except KeyboardInterrupt:
            consumer.close()