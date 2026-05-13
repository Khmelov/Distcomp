using Publisher.src.NewsPortal.Publisher.Application.Dtos;

namespace Publisher.src.NewsPortal.Publisher.Infrastructure.Messaging
{
    public interface IKafkaProducerService
    {
        Task SendAsync(string topic, KafkaNoteMessage message);
    }
}
