using System.Collections.Concurrent;
using System.Text.Json;
using Confluent.Kafka;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;

namespace Additions.Service.EventService;

public class KafkaProducerService : IEventProducerService, IDisposable
{
    private readonly IProducer<string, string> producer;
    private readonly IConsumer<string, string> consumer;
    private readonly string recvTopic;
    private bool isDisposed = false;
    private readonly ConcurrentDictionary<string, TaskCompletionSource<string>> pendingRequests = [];
    private readonly CancellationTokenSource cts = new();
    private readonly Task consumerTask;
    private readonly ILogger<KafkaProducerService> logger;

    public KafkaProducerService(IConfiguration configuration, ILogger<KafkaProducerService> logger)
    {
        this.logger = logger;

        string bootstrapServers = configuration["Kafka:BootstrapServers"] ?? "localhost:9092";
        producer = new ProducerBuilder<string, string>(new ProducerConfig()
        {
            BootstrapServers = bootstrapServers,
            AllowAutoCreateTopics = true
        }).Build();

        recvTopic = configuration["Kafka:RecvTopic"] ?? "default-topic";
        string groupId = configuration["Kafka:GroupId"] ?? "default-group";
        ConsumerConfig consumerConfig = new()
        {
            BootstrapServers = bootstrapServers,
            GroupId = groupId,
            AutoOffsetReset = AutoOffsetReset.Earliest,
            AllowAutoCreateTopics = true,
            EnableAutoCommit = false
        };
        consumer = new ConsumerBuilder<string, string>(consumerConfig).Build();
        consumer.Subscribe(recvTopic);

        consumerTask = Task.Run(() => ProcessResponsesAsync(cts.Token));
    }

    public async Task ProduceEventAsync<T>(string topic, EventMessage<T> message)
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

    public async Task<EventMessage<X>> ProduceEventWithResponseAsync<T, X>(string topic, EventMessage<T> message, TimeSpan timeout)
    {
        TaskCompletionSource<string> tcs = new();
        string messageId = message.MessageId.ToString();

        pendingRequests[messageId] = tcs;

        try
        {
            await ProduceEventAsync<T>(topic, message);

            var timeoutTask = Task.Delay(timeout);
            var completed = await Task.WhenAny(tcs.Task, timeoutTask);

            if (completed == timeoutTask)
            {
                throw new ServiceException(
                    $"No response received for MessageId {messageId} within {timeout}");
            }

            string responseJson = await tcs.Task;
            EventMessage<X>? responseMessage = JsonSerializer.Deserialize<EventMessage<X>>(responseJson);
            if (responseMessage == null)
            {
                throw new ServiceException("Empty response from Kafka");
            }

            return responseMessage;
        }
        finally
        {
            pendingRequests.TryRemove(messageId, out _);
        }
    }

    private void ProcessResponsesAsync(CancellationToken token)
    {
        while (!token.IsCancellationRequested)
        {
            try
            {
                var consumeResult = consumer.Consume(TimeSpan.FromMilliseconds(100));
                if (consumeResult?.Message != null)
                {
                    var messageId = consumeResult.Message.Key;
                    if (pendingRequests.TryGetValue(messageId, out var tcs))
                    {
                        EventMessage<string>? responseMessage = JsonSerializer.Deserialize<EventMessage<string>>(consumeResult.Message.Value);
                        if (responseMessage != null) {
                            if (!string.IsNullOrEmpty(responseMessage.Error))
                            {
                                tcs.TrySetException(new ServiceFailedOperationException(responseMessage.Error));
                            }
                            else
                            {
                                tcs.TrySetResult(consumeResult.Message.Value);
                            }
                        }
                        else
                        {
                            tcs.TrySetException(new ServiceFailedOperationException("Response message is corrupted."));
                        }
                    }
                }
            }
            catch (ConsumeException e)
            {
                logger.LogWarning($"[Kafka] Consume error: {e.Error.Reason}");
            }
        }
    }

    public void Dispose()
    {
        if (!isDisposed)
        {
            isDisposed = true;
            cts.Cancel();
            
            try
            {
                consumerTask.Wait(TimeSpan.FromSeconds(2));
            }
            catch {}
            
            producer.Flush(TimeSpan.FromSeconds(15));
            producer.Dispose();
            consumer.Close();
            consumer.Dispose();
            GC.SuppressFinalize(this);
        }
    }
}