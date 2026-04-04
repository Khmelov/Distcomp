using System.Text.Json;
using Additions.Service.EventService.Interfaces;
using Confluent.Kafka;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;

namespace Additions.Service.EventService.Implementations;

public class KafkaProducerService : IEventProducerService, IDisposable
{
    private readonly IProducer<string, string> producer;
    private bool isDisposed = false;
    private readonly IEventOrchestratorService eventOrchestrator;

    public KafkaProducerService(IConfiguration configuration, IEventOrchestratorService eventOrchestrator)
    {
        this.eventOrchestrator = eventOrchestrator;

        string bootstrapServers = configuration["Kafka:BootstrapServers"] ?? "localhost:9092";
        producer = new ProducerBuilder<string, string>(new ProducerConfig()
        {
            BootstrapServers = bootstrapServers,
            AllowAutoCreateTopics = true
        }).Build();
    }

    public async Task ProduceEventAsync(string topic, EventMessage message)
    {
        try
        {
            var deliveryResult = await producer.ProduceAsync(topic, new Message<string, string>
            {
                Key = message.MessageId.ToString(),
                Value = JsonSerializer.Serialize(message)
            });
        }
        catch (ProduceException<string, string> e)
        {
            
            throw new ServiceException($"Failed to produce message to {topic}: {e.Error.Reason}");
        }
    }

    public async Task<EventMessage> ProduceEventWithResponseAsync(string topic, EventMessage message, TimeSpan timeout)
    {
        var responseTask = eventOrchestrator.ExpectResponse(message.MessageId);
        await ProduceEventAsync(topic, message);
        EventMessage result = await responseTask;
        if (result.Error != null)
        {
            throw new ServiceFailedOperationException(result.Error);
        }
        return result;
    }

    public void Dispose()
    {
        if (!isDisposed)
        {
            isDisposed = true;
            producer.Flush(TimeSpan.FromSeconds(15));
            producer.Dispose();
            GC.SuppressFinalize(this);
        }
    }
}