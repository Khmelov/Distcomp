using Confluent.Kafka;

namespace Messaging.Producer;

public class KafkaProducerConfig : ProducerConfig
{
    public string Topic { get; set; }
}