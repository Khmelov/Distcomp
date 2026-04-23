using System.Text.Json;
using Additions.Messaging.Interfaces;
using Confluent.Kafka;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;

namespace Additions.Messaging.Implementations;

public class KafkaConsumer : BaseEventConsumer
{
    private readonly string topic;
    private readonly IConsumer<string, string> consumer;
    private readonly ILogger<KafkaConsumer> logger;
    private readonly IEventOrchestrator eventOrchestrator;
    private readonly Dictionary<string, IEventHandler> handlerMap = [];

    private void InitHandlerMap(IEnumerable<IEventHandler> handlers)
    {
        foreach (IEventHandler handler in handlers)
        {
            handlerMap[handler.SupportedOperation] = handler;
        }
    }

    public KafkaConsumer(IConfiguration configuration, ILogger<KafkaConsumer> logger,
                         IEventOrchestrator eventOrchestrator, IEnumerable<IEventHandler> eventHandlers)
    {
        this.logger = logger;
        this.eventOrchestrator = eventOrchestrator;

        topic = configuration["Kafka:RecvTopic"] ?? "default-topic";
        string bootstrapServers = configuration["Kafka:BootstrapServers"] ?? "localhost:9092";
        string groupId = configuration["Kafka:GroupId"] ?? "default-group";
        InitHandlerMap(eventHandlers);

        ConsumerConfig consumerConfig = new()
        {
            BootstrapServers = bootstrapServers,
            GroupId = groupId,
            AutoOffsetReset = AutoOffsetReset.Earliest,
            AllowAutoCreateTopics = true,
            EnableAutoCommit = false
        };
        consumer = new ConsumerBuilder<string, string>(consumerConfig).Build();
        consumer.Subscribe(topic);
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        consumer.Subscribe(topic);
        try
        {
            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    var consumeResult = consumer.Consume(stoppingToken);
                    if (consumeResult != null)
                    {
                        await HandleMessageAsync(consumeResult.Message.Key, consumeResult.Message.Value);
                        consumer.Commit(consumeResult);
                    }
                }
                catch (ConsumeException e)
                {
                    logger.LogError(e, $"Consumption error: {e.Error.Reason}");
                    await Task.Delay(1000, stoppingToken);
                }
                catch (OperationCanceledException)
                {
                    break;
                }
            }
        }
        finally
        {
            consumer.Close();
        }
    }

    private async Task HandleMessageAsync(string key, string value)
    {
        EventMessage? message = JsonSerializer.Deserialize<EventMessage>(value);
        if (message != null)
        {
            if (message.InReplyTo != null)
            {
                eventOrchestrator.ResolveResponse(message);
            }
            else
            {
                if (handlerMap.TryGetValue(message.Operation, out IEventHandler? handler))
                {
                    await handler.HandleMessage(message);
                }
                else {
                    logger.LogInformation($"There is not a handler for this message: Key={key}, Value={value}.");
                }
            }
        } 
        else
        {
            logger.LogInformation($"Got an invalid message from Kafka: Key={key}, Value={value}");
        }
    }
}