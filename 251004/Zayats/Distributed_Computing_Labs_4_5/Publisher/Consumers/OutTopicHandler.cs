using Messaging;
using Messaging.Consumer.Interfaces;
using Publisher.DTO.ResponseDTO;

namespace Publisher.Consumers;

public class OutTopicHandler : IKafkaHandler<string, KafkaMessage<MessageResponseDTO>>
{
    public async Task HandleAsync(string key, KafkaMessage<MessageResponseDTO> value)
    {
    }
}