using Shared.Domain.DTOs;

namespace Shared.Domain.Interfaces;

public interface ICommentMessageProducer {
    Task SendCommentAsync(CommentKafkaMessage message);
}