using System.Text.Json;
using Confluent.Kafka;
using Publisher.Application.Services;
using Shared.Constants;
using Shared.Messaging;

namespace Publisher.Presentation.Background;

public class KafkaResponseListener : BackgroundService
{
    private readonly IConsumer<string, string> _consumer;

    public KafkaResponseListener(IConsumer<string, string> consumer)
    {
        _consumer = consumer;
    }

    protected override Task ExecuteAsync(CancellationToken stoppingToken)
    {
        
        return Task.Run(() =>
        {
            _consumer.Subscribe(KafkaTopics.OutTopic);

            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    var result = _consumer.Consume(stoppingToken);
                    var response = JsonSerializer.Deserialize<KafkaResponse>(result.Message.Value);
                    
                    if (response != null)
                    {
                        
                        KafkaCommentService.HandleResponse(response);
                    }
                }
                catch (OperationCanceledException) { break; }
                catch (Exception ex)
                {
                    Console.WriteLine($"Error reading from OutTopic: {ex.Message}");
                }
            }
        }, stoppingToken);
    }
}