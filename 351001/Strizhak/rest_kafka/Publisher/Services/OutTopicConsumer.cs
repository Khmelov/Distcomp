using Confluent.Kafka;
using Publisher.Dtos;
using Publisher.Proxies;
using System.Text.Json;

namespace Publisher.Services
{
    public class OutTopicConsumer : BackgroundService
    {
        private readonly IConsumer<string, string> _consumer;
        private readonly ILogger<OutTopicConsumer> _logger;

        public OutTopicConsumer(ILogger<OutTopicConsumer> logger)
        {
            _logger = logger;
            var config = new ConsumerConfig
            {
                BootstrapServers = "localhost:9092",
                GroupId = "publisher-group",
                AutoOffsetReset = AutoOffsetReset.Earliest
            };
            _consumer = new ConsumerBuilder<string, string>(config).Build();
            _consumer.Subscribe("OutTopic");
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    var cr = _consumer.Consume(stoppingToken);
                    var reaction = JsonSerializer.Deserialize<ReactionResponseTo>(cr.Message.Value);
                    if (reaction != null)
                    {
                        ReactionProxy.TryCompletePending(reaction.Id, reaction);
                    }
                }
                catch (Exception ex)
                {
                    _logger.LogError(ex, "Ошибка в consumer OutTopic");
                }
            }
        }

        public override void Dispose()
        {
            _consumer.Close();
            _consumer.Dispose();
            base.Dispose();
        }
    }
}