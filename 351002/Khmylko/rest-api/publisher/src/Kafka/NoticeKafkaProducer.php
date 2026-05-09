<?php
declare(strict_types=1);

namespace App\Kafka;

class NoticeKafkaProducer {
    private ?\RdKafka\Producer $producer = null;
    private ?\RdKafka\Topic $topic = null;

    public function __construct() {
        if (!class_exists(\RdKafka\Producer::class)) {
            return;
        }
        $brokers = KafkaNoticeIo::brokers();
        if ($brokers === '') {
            return;
        }
        $conf = new \RdKafka\Conf();
        $this->producer = new \RdKafka\Producer($conf);
        $this->producer->addBrokers($brokers);
        $this->topic = $this->producer->newTopic(KafkaNoticeIo::TOPIC_IN);
    }

    public function isEnabled(): bool {
        return $this->producer !== null && $this->topic !== null;
    }

    /** Partition: same tweetId => same partition (message key). */
    public function publishPendingNotice(array $notice): void {
        if (!$this->isEnabled()) {
            return;
        }
        $tweetId = (int)($notice['tweetId'] ?? $notice['tweet_id'] ?? 0);
        if ($tweetId <= 0) {
            return;
        }
        $payload = json_encode([
            'id' => (int)($notice['id'] ?? 0),
            'tweetId' => $tweetId,
            'content' => (string)($notice['content'] ?? ''),
            'state' => $notice['state'] ?? 'PENDING',
        ], JSON_THROW_ON_ERROR | JSON_UNESCAPED_UNICODE);
        $this->topic->produce(RD_KAFKA_PARTITION_UA, 0, $payload, (string)$tweetId);
        $this->producer->poll(0);
        while ($this->producer->getOutQLen() > 0) {
            $this->producer->poll(1);
        }
    }
}
