using Confluent.Kafka;
using Distcomp.Shared.Models;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Configuration;
using System.Collections.Concurrent;
using System.Text.Json;

namespace Distcomp.Infrastructure.Messaging
{
    public class KafkaRequestReplyService : BackgroundService
    {
        private readonly IConfiguration _config;
        private readonly IProducer<string, string> _producer;
        private readonly IConsumer<string, string> _consumer;

        private readonly ConcurrentDictionary<string, TaskCompletionSource<string>> _pendingRequests = new();

        public KafkaRequestReplyService(IConfiguration config)
        {
            _config = config;
            var bootstrapServers = _config["Kafka:BootstrapServers"] ?? "localhost:9092";

            _producer = new ProducerBuilder<string, string>(new ProducerConfig { BootstrapServers = bootstrapServers }).Build();

            _consumer = new ConsumerBuilder<string, string>(new ConsumerConfig
            {
                BootstrapServers = bootstrapServers,
                GroupId = "publisher-replies-" + Guid.NewGuid(),
                AutoOffsetReset = AutoOffsetReset.Latest
            }).Build();
        }

        public async Task<string?> SendRequestAsync(NoteOperationMessage request, int timeoutMs = 1000)
        {
            var tcs = new TaskCompletionSource<string>();
            _pendingRequests[request.CorrelationId] = tcs;

            var json = JsonSerializer.Serialize(request);
            await _producer.ProduceAsync("InTopic", new Message<string, string>
            {
                Key = request.IssueId?.ToString() ?? "0",
                Value = json
            });

            var completedTask = await Task.WhenAny(tcs.Task, Task.Delay(timeoutMs));

            if (completedTask == tcs.Task)
            {
                return await tcs.Task;
            }

            _pendingRequests.TryRemove(request.CorrelationId, out _);
            return null;
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            _consumer.Subscribe("OutTopic");
            await Task.Yield();

            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    var result = _consumer.Consume(stoppingToken);
                    if (result?.Message != null)
                    {
                        using var doc = JsonDocument.Parse(result.Message.Value);
                        string? correlationId = null;
                        if (doc.RootElement.TryGetProperty("CorrelationId", out var idProp) ||
                            doc.RootElement.TryGetProperty("correlationId", out idProp))
                        {
                            correlationId = idProp.GetString();
                        }

                        if (correlationId != null && _pendingRequests.TryRemove(correlationId, out var tcs))
                        {
                            tcs.SetResult(result.Message.Value);
                        }
                    }
                }
                catch (Exception ex) { Console.WriteLine("Reply Consumer Error: " + ex.Message); }
            }
        }
    }
}