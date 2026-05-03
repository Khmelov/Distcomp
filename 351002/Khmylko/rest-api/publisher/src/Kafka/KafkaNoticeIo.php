<?php
declare(strict_types=1);

namespace App\Kafka;

class KafkaNoticeIo {
    public const TOPIC_IN = 'InTopic';
    public const TOPIC_OUT = 'OutTopic';

    public static function brokers(): string {
        return getenv('KAFKA_BROKERS') ?: 'kafka:29092';
    }

    public static function groupOut(): string {
        return getenv('KAFKA_GROUP_OUT') ?: 'publisher-out';
    }
}
