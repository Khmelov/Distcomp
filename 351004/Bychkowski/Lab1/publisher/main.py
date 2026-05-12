import uvicorn
import json
import threading
import time
import redis
from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError
from kafka import KafkaConsumer

import models
from database import init_db
from routers import router
from routers_v2 import router_v2
from exceptions import AppError, app_exception_handler, validation_exception_handler

init_db()

app = FastAPI(title="Publisher Microservice (Secured & Cached)")
app.include_router(router)
app.include_router(router_v2)
app.add_exception_handler(AppError, app_exception_handler)
app.add_exception_handler(RequestValidationError, validation_exception_handler)


def kafka_out_topic_worker():
    for _ in range(30):
        try:
            consumer = KafkaConsumer(
                'OutTopic',
                bootstrap_servers=['localhost:9092'],
                group_id='publisher-group-live',
                value_deserializer=lambda x: json.loads(x.decode('utf-8'))
            )
            r = None
            try:
                r = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)
            except:
                pass

            for message in consumer:
                data = message.value
                post_id = data.get('id')
                state = data.get('state')

                if post_id and r:
                    try:
                        cached = r.get(f"post:{post_id}")
                        if cached:
                            post_data = json.loads(cached)
                            post_data['state'] = state
                            r.set(f"post:{post_id}", json.dumps(post_data))

                        r.delete("post_all")
                    except:
                        pass
            break
        except Exception:
            time.sleep(1)


@app.on_event("startup")
def startup_event():
    threading.Thread(target=kafka_out_topic_worker, daemon=True).start()


if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=24110, reload=True)