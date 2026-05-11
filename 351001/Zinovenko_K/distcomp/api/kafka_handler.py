import json
import uuid
import time
import threading
import redis
from confluent_kafka import Producer, Consumer
from django.conf import settings

cache_db = redis.Redis(
    host='127.0.0.1',
    port=6379,
    db=0,
    decode_responses=True
)

IN_TOPIC = 'InTopic'
OUT_TOPIC = 'OutTopic'

KAFKA_CONF = {'bootstrap.servers': 'localhost:9092'}
producer = Producer(KAFKA_CONF)
results_storage = {}


def response_listener():
    consumer = Consumer({
        **KAFKA_CONF,
        'group.id': f'publisher-redis-group-{uuid.uuid4()}',
        'auto.offset.reset': 'latest'
    })
    consumer.subscribe([OUT_TOPIC])
    while True:
        msg = consumer.poll(0.1)
        if msg is None or msg.error(): continue
        try:
            data = json.loads(msg.value().decode('utf-8'))
            corr_id = data.get('correlation_id')
            if corr_id:
                results_storage[corr_id] = (data.get('result'), data.get('status'))
        except:
            continue


threading.Thread(target=response_listener, daemon=True).start()


def call_kafka_sync(action, data, timeout=1.5):
    msg_id = data.get('id')
    cache_key = f"message:{msg_id}"

    if action == 'GET' and msg_id:
        cached = cache_db.get(cache_key)
        if cached:
            return json.loads(cached), 200

    correlation_id = str(uuid.uuid4())
    payload = {'action': action, 'correlation_id': correlation_id, 'data': data}
    producer.produce(IN_TOPIC, json.dumps(payload).encode('utf-8'))
    producer.flush()

    start_time = time.time()
    while time.time() - start_time < timeout:
        if correlation_id in results_storage:
            result, status = results_storage.pop(correlation_id)

            if status == 200 and action in ['GET', 'PUT']:
                cache_db.setex(cache_key, 300, json.dumps(result))

            if action == 'DELETE':
                cache_db.delete(cache_key)

            return result, status
        time.sleep(0.02)

    return {"detail": "Timeout"}, 504
