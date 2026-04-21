namespace Publisher.Services
{
    public interface IKafkaProducer
    {
        Task ProduceAsync(string topic, string key, object message);
    }
}
