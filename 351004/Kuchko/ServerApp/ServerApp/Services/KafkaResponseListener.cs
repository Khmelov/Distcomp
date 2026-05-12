using System.Text.Json;
using Confluent.Kafka;
using SharedModels;

namespace ServerApp.Services;

public class KafkaResponseListener(KafkaRequestManager requestManager, IConfiguration config) : BackgroundService
{
    protected override Task ExecuteAsync(CancellationToken stoppingToken)
    {
        return Task.Run(() =>
        {
            var conf = new ConsumerConfig
            {
                BootstrapServers = config["Kafka:BootstrapServers"] ?? "kafka:29092",
                GroupId = "publisher-response-group",
                AutoOffsetReset = AutoOffsetReset.Earliest
            };

            using var consumer = new ConsumerBuilder<string, string>(conf).Build();
            consumer.Subscribe("OutTopic");

            while (!stoppingToken.IsCancellationRequested)
                try
                {
                    var result = consumer.Consume(stoppingToken);
                    var @event = JsonSerializer.Deserialize<KafkaEvent>(result.Message.Value);
                    if (@event != null) requestManager.Resolve(@event);
                }
                catch
                {
                    /* log error */
                }
        }, stoppingToken);
    }
}