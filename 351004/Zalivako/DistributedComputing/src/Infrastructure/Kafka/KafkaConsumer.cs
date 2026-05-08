using Confluent.Kafka;
using Core.Entities;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Configuration;
using System.Text.Json;
using Application.Interfaces;
using Microsoft.Extensions.Caching.Distributed;

namespace Infrastructure.Kafka
{
    public class KafkaPostConsumer : BackgroundService
    {
        private readonly IConsumer<string, string> _consumer;
        private readonly IServiceScopeFactory _serviceScopeFactory;
        private readonly ILogger<KafkaPostConsumer> _logger;
        private readonly string _outTopic;

        public KafkaPostConsumer(IConfiguration configuration,
                                 IServiceScopeFactory serviceScopeFactory,
                                 ILogger<KafkaPostConsumer> logger)
        {
            _serviceScopeFactory = serviceScopeFactory;
            _logger = logger;

            var config = new ConsumerConfig
            {
                BootstrapServers = configuration["Kafka:BootstrapServers"],
                GroupId = "publisher-post-status-group",
                AutoOffsetReset = AutoOffsetReset.Earliest,
                EnableAutoCommit = true
            };
            _consumer = new ConsumerBuilder<string, string>(config).Build();
            _outTopic = configuration["Kafka:OutTopic"] ?? "OutTopic";
        }

        protected override Task ExecuteAsync(CancellationToken stoppingToken)
        {
            _consumer.Subscribe(_outTopic);
            return Task.Run(async () =>
            {
                while (!stoppingToken.IsCancellationRequested)
                {
                    try
                    {
                        var consumeResult = _consumer.Consume(stoppingToken);
                        await ProcessMessageAsync(consumeResult.Message.Value, stoppingToken);
                    }
                    catch (OperationCanceledException) { break; }
                    catch (Exception ex)
                    {
                        _logger.LogError(ex, "Error consuming Kafka message");
                    }
                }
            }, stoppingToken);
        }

        private async Task ProcessMessageAsync(string messageValue, CancellationToken cancellationToken)
        {
            try
            {
                var statusUpdate = JsonSerializer.Deserialize<PostStatusUpdate>(messageValue);
                if (statusUpdate == null) return;

                using var scope = _serviceScopeFactory.CreateScope();
                var postRepo = scope.ServiceProvider.GetRequiredService<IPostRepository>();
                var cache = scope.ServiceProvider.GetRequiredService<IDistributedCache>();

                var post = await postRepo.GetByIdAsync(statusUpdate.PostId, cancellationToken);
                if (post != null)
                {
                    post.State = Enum.Parse<PostState>(statusUpdate.NewState);
                    await postRepo.UpdateAsync(post, cancellationToken);

                    // Инвалидация кэша для этого поста и общего списка
                    await cache.RemoveAsync("posts_all");
                    await cache.RemoveAsync($"post_{post.Id}");

                    _logger.LogInformation("Updated post {PostId} state to {State}", post.Id, post.State);
                }
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error processing message: {Message}", messageValue);
            }
        }

        public override void Dispose()
        {
            _consumer?.Dispose();
            base.Dispose();
        }

        private class PostStatusUpdate
        {
            public long PostId { get; set; }
            public string NewState { get; set; } = string.Empty;
        }
    }
}