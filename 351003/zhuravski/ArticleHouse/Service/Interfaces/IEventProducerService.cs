namespace ArticleHouse.Service.Interfaces;

public interface IEventProducerService
{
    Task ProduceEventAsync(string topic, string key, string value);
}