from confluent_kafka import Producer, Consumer
import json
import logging
from moderator import moderate

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class KafkaService:
    def __init__(self):
        self.producer = Producer({
            'bootstrap.servers': 'kafka:9092',
            'client.id': 'discussion-producer'
        })

        self.consumer = Consumer({
            'bootstrap.servers': 'kafka:9092',
            'group.id': 'discussion-group',
            'auto.offset.reset': 'earliest'
        })
        self.consumer.subscribe(['ReactionInTopic'])

    def process_reactions(self):
        while True:
            msg = self.consumer.poll(1.0)
            if msg is None:
                continue
            if msg.error():
                logger.error(f"Consumer error: {msg.error()}")
                continue

            try:
                reaction_data = json.loads(msg.value().decode('utf-8'))
                # Moderate reaction
                reaction_data['state'] = moderate(reaction_data['content']).value

                # Send back to publisher
                self.producer.produce(
                    'ReactionOutTopic',
                    key=str(reaction_data['news_id']),
                    value=json.dumps(reaction_data),
                    callback=self._delivery_report
                )
                self.producer.flush()
                logger.info(f"Processed reaction for news {reaction_data['news_id']}")
            except Exception as e:
                logger.error(f"Error processing message: {e}")

    @staticmethod
    def _delivery_report(err, msg):
        if err:
            logger.error(f'Message delivery failed: {err}')
        else:
            logger.info(f'Message delivered to {msg.topic()} [{msg.partition()}]')