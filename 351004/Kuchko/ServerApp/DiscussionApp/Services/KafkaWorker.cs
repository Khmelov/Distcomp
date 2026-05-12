using System.Text.Json;
using Confluent.Kafka;
using DiscussionApp.Repositories;
using SharedModels;

namespace DiscussionApp.Services;

public class KafkaWorker(MessageRepository repo, IConfiguration config) : BackgroundService
{
    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        var bootstrap = config["Kafka:BootstrapServers"] ?? "kafka:29092";
        var cConf = new ConsumerConfig
            { BootstrapServers = bootstrap, GroupId = "dis-group", AutoOffsetReset = AutoOffsetReset.Earliest };
        var pConf = new ProducerConfig { BootstrapServers = bootstrap };

        using var consumer = new ConsumerBuilder<string, string>(cConf).Build();
        using var producer = new ProducerBuilder<string, string>(pConf).Build();
        consumer.Subscribe("InTopic");

        while (!stoppingToken.IsCancellationRequested)
        {
            var result = consumer.Consume(stoppingToken);
            var @event = JsonSerializer.Deserialize<KafkaEvent>(result.Message.Value);
            if (@event == null) continue;

            try
            {
                switch (@event.Action)
                {
                    case "GET_ALL":
                        @event.Payload = JsonSerializer.Serialize(repo.GetAll());
                        break;
                    case "GET":
                        var gMsg = repo.GetById(long.Parse(@event.Payload));
                        @event.Payload = gMsg != null ? JsonSerializer.Serialize(gMsg) : "";
                        if (gMsg == null) @event.ErrorMessage = "Not Found";
                        break;
                    case "CREATE":
                        var cDto = JsonSerializer.Deserialize<MessageResponseTo>(@event.Payload)!;
                        var state = cDto.Content.Contains("spam") ? MessageState.Decline : MessageState.Approve;
                        var finalMsg = cDto with { State = state };
                        repo.Create(finalMsg);
                        @event.Payload = JsonSerializer.Serialize(finalMsg);
                        break;
                    case "UPDATE":
                        var uDto = JsonSerializer.Deserialize<MessageResponseTo>(@event.Payload)!;
                        repo.Update(uDto);
                        break;
                    case "DELETE":
                        repo.Delete(long.Parse(@event.Payload), @event.ArticleId);
                        break;
                }
            }
            catch (Exception ex)
            {
                @event.ErrorMessage = ex.Message;
            }

            await producer.ProduceAsync("OutTopic", new Message<string, string>
            {
                Key = @event.ArticleId.ToString(), Value = JsonSerializer.Serialize(@event)
            });
        }
    }
}