using Core.Entities;

namespace Application.Interfaces
{
    public interface IKafkaProducer
    {
        Task SendPostAsync(Post post);
    }
}