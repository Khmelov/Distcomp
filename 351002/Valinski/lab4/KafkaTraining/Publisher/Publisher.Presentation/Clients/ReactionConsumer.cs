using System.Text;
using Confluent.Kafka;
using Microsoft.Extensions.Options;
using Publisher.Presentation.Options;

namespace Publisher.Presentation.Clients;

public class ReactionConsumer : BackgroundService
{
    private readonly IConsumer<string, string> _consumer;
    private readonly KafkaReactionConnectionOptions _kafkaReactionConnectionOptions;
    private readonly ReactionsServiceClient _reactionsServiceClient;

    public ReactionConsumer(IConsumer<string, string> consumer,
        IOptions<KafkaReactionConnectionOptions> kafkaReactionConnectionOptions,
        ReactionsServiceClient reactionsServiceClient)
    {
        _consumer = consumer;
        _reactionsServiceClient = reactionsServiceClient;
        _kafkaReactionConnectionOptions = kafkaReactionConnectionOptions.Value;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        Task.Run(async () =>
        {
            _consumer.Subscribe(_kafkaReactionConnectionOptions.OutTopic);
            try
            {
                while (!stoppingToken.IsCancellationRequested)
                {
                    var res = _consumer.Consume(stoppingToken);
                    
                    var headerBytes = res.Message.Headers
                        .FirstOrDefault(x => x.Key == "CorrelationId")?.GetValueBytes();

                    if (headerBytes != null)
                    {
                        var correlationId = Encoding.UTF8.GetString(headerBytes);
                        _reactionsServiceClient.SendResult(correlationId, res.Message.Value);
                    }
                }
            }
            catch (OperationCanceledException) { /* Игнорируем при остановке */ }
            finally 
            { 
                _consumer.Close(); 
            }
        });
    }
}
