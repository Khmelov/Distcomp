using Discussion.src.NewsPortal.Discussion.Domain.Entities;

namespace Discussion.src.NewsPortal.Discussion.Infrastructure.Messaging
{
    public interface IKafkaProducerService
    {
        Task SendAsync(string topic, KafkaNoteMessage message);
    }
}
