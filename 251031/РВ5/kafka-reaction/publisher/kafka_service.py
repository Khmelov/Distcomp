from confluent_kafka import Producer, Consumer
import json
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class KafkaService:
    def __init__(self):
        self.producer = Producer({
            'bootstrap.servers': 'kafka:9092',
            'client.id': 'publisher-producer'
        })

        self.consumer = Consumer({
            'bootstrap.servers': 'kafka:9092',
            'group.id': 'publisher-group',
            'auto.offset.reset': 'earliest'
        })
        self.consumer.subscribe(['ReactionOutTopic'])

    def produce_reaction(self, reaction_data):
        try:
            self.producer.produce(
                'ReactionInTopic',
                key=str(reaction_data['news_id']),
                value=json.dumps(reaction_data),
                callback=self._delivery_report
            )
            self.producer.flush()
            logger.info(f"Produced reaction for news {reaction_data['news_id']}")
        except Exception as e:
            logger.error(f"Error producing message: {e}")

    def consume_updates(self, callback):
        while True:
            msg = self.consumer.poll(1.0)
            if msg is None:
                continue
            if msg.error():
                logger.error(f"Consumer error: {msg.error()}")
                continue

            try:
                reaction_data = json.loads(msg.value().decode('utf-8'))
                callback(reaction_data)
            except Exception as e:
                logger.error(f"Error processing message: {e}")

    @staticmethod
    def _delivery_report(err, msg):
        if err:
            logger.error(f'Message delivery failed: {err}')
        else:
            logger.info(f'Message delivered to {msg.topic()} [{msg.partition()}]')