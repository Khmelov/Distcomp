namespace Messaging;

public class KafkaMessage<T>
{
    public string RequestId { get; set; }
    public string OperationType { get; set; }
    public T Data { get; set; }
}