using Confluent.Kafka;

namespace Messaging.Consumer;

public class KafkaConsumerConfig : ConsumerConfig
{
    public string Topic { get; set; }
}