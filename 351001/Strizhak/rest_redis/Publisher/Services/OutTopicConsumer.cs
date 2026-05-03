using Confluent.Kafka;
using Publisher.Dtos;
using Publisher.Dtos.Kafka;
using System.Diagnostics;
using System.Text.Json;

namespace Publisher.Services
{
    public class OutTopicConsumer : BackgroundService
    {
        private readonly IConsumer<string, string> _consumer;
        private readonly ILogger<OutTopicConsumer> _logger;
        private readonly JsonSerializerOptions _jsonOptions = new() { PropertyNameCaseInsensitive = true };
        private readonly KafkaResponseTracker _tracker;

        public OutTopicConsumer(ILogger<OutTopicConsumer> logger, KafkaResponseTracker tracker)
        {
            _logger = logger;
            _tracker = tracker;
            var config = new ConsumerConfig
            {
                BootstrapServers = "localhost:9092",
                GroupId = "publisher-group-v1", 
                AutoOffsetReset = AutoOffsetReset.Earliest,
                AllowAutoCreateTopics = true
            };
            _consumer = new ConsumerBuilder<string, string>(config).Build();
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            await Task.Yield();
            _consumer.Subscribe("OutTopic");

            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    var cr = _consumer.Consume(stoppingToken);
                    if (cr?.Message == null) continue;

                    _logger.LogInformation($"Kafka Receive: {cr.Message.Value}");

                    var msg = JsonSerializer.Deserialize<KafkaMessage>(cr.Message.Value);
                    if (msg != null && !string.IsNullOrEmpty(msg.CorrelationId))
                    {
                        _tracker.CompleteResponse(msg.CorrelationId, msg.Payload.GetRawText());
                    }
                }
                catch (OperationCanceledException) { break; }
                catch (Exception ex)
                {
                    _logger.LogError(ex, "Error in OutTopicConsumer");
                }
            }
        }
    }
}