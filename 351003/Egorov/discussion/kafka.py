import subprocess
from .validate import validate
import json


def kafka_write(a):
    with open('/tmp/kafka_msg.txt', 'w') as f:
        f.write(json.dumps(a))

    r = subprocess.run(
        'cat /tmp/kafka_msg.txt | docker exec -i kafka /opt/kafka/bin/kafka-console-producer.sh '
        '--bootstrap-server localhost:9092 --topic InTopic',
        shell=True, capture_output=True, text=True
    )

def kafka_listen(a):
    with open('/tmp/kafka_msg.txt', 'w') as f:
        f.write(json.dumps(a))

    r = subprocess.run(
        'cat /tmp/kafka_msg.txt | docker exec -i kafka /opt/kafka/bin/kafka-console-producer.sh '
        '--bootstrap-server localhost:9092 --topic OutTopic',
        shell=True, capture_output=True, text=True
    )

























def use(a):
    kafka_write(a)
    if validate(a["content"]):
        a["state"] = "APPROVE"
        kafka_listen(a)
        return True
    else:
        a["state"] = "DELCINE"
        kafka_listen(a)
        return False