<?php
declare(strict_types=1);

namespace Discussion\Kafka;

class KafkaNoticeIo {
    public const TOPIC_IN = 'InTopic';
    public const TOPIC_OUT = 'OutTopic';

    public static function brokers(): string {
        return getenv('KAFKA_BROKERS') ?: 'kafka:29092';
    }

    public static function groupIn(): string {
        return getenv('KAFKA_GROUP_IN') ?: 'discussion-in';
    }

    /** @return array<string, mixed>|null */
    public static function decodePayload(string $json): ?array {
        try {
            $data = json_decode($json, true, 512, JSON_THROW_ON_ERROR);
            return is_array($data) ? $data : null;
        } catch (\Throwable) {
            return null;
        }
    }

    public static function encodePayload(array $payload): string {
        return json_encode($payload, JSON_THROW_ON_ERROR | JSON_UNESCAPED_UNICODE);
    }
}
