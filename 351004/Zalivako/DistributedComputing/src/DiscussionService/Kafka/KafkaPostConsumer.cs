using Confluent.Kafka;
using DiscussionService.Interfaces;
using DiscussionService.Models;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Configuration;
using System.Text.Json;
using Microsoft.Extensions.DependencyInjection;

namespace DiscussionService.Kafka
{
    public class KafkaPostConsumer : BackgroundService
    {
        private readonly IConsumer<string, string> _consumer;
        private readonly IServiceScopeFactory _serviceScopeFactory;
        private readonly ILogger<KafkaPostConsumer> _logger;
        private readonly string _inTopic;

        public KafkaPostConsumer(IConfiguration configuration, IServiceScopeFactory serviceScopeFactory, ILogger<KafkaPostConsumer> logger)
        {
            _serviceScopeFactory = serviceScopeFactory;
            _logger = logger;

            var config = new ConsumerConfig
            {
                BootstrapServers = configuration["Kafka:BootstrapServers"],
                GroupId = "discussion-post-group",
                AutoOffsetReset = AutoOffsetReset.Earliest,
                EnableAutoCommit = true
            };

            _consumer = new ConsumerBuilder<string, string>(config).Build();
            _inTopic = configuration["Kafka:InTopic"] ?? "InTopic";
        }

        protected override Task ExecuteAsync(CancellationToken stoppingToken)
        {
            _consumer.Subscribe(_inTopic);

            return Task.Run(async () =>
            {
                while (!stoppingToken.IsCancellationRequested)
                {
                    try
                    {
                        var consumeResult = _consumer.Consume(stoppingToken);
                        await ProcessMessageAsync(consumeResult.Message.Value, stoppingToken);
                    }
                    catch (OperationCanceledException)
                    {
                        break;
                    }
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
                var postMessage = JsonSerializer.Deserialize<PostMessage>(messageValue);
                if (postMessage == null) return;

                using var scope = _serviceScopeFactory.CreateScope();
                var postService = scope.ServiceProvider.GetRequiredService<IPostService>();
                var kafkaProducer = scope.ServiceProvider.GetRequiredService<IKafkaProducer>();

                // Сохраняем/обновляем пост в MongoDB
                var post = new Post
                {
                    Id = postMessage.Id,
                    NewsId = postMessage.NewsId,
                    Content = postMessage.Content,
                    Country = postMessage.Country,
                    State = PostState.PENDING
                };

                // Проверяем, существует ли уже пост (чтобы не дублировать при обновлении)
                var existing = await postService.GetPostById(post.Id);
                if (existing == null)
                {
                    await postService.CreatePostInternalAsync(post);
                }
                else
                {
                    await postService.UpdatePostInternalAsync(post);
                }

                // Модерация
                var moderatedState = ModerateContent(post.Content);
                post.State = moderatedState;

                // Обновляем статус в MongoDB
                await postService.UpdatePostStateAsync(post.Id, moderatedState);

                // Отправляем результат в OutTopic
                await kafkaProducer.SendStatusUpdateAsync(post.Id, moderatedState);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error processing message: {Message}", messageValue);
            }
        }

        private PostState ModerateContent(string content)
        {
            // Простейшая модерация: если есть стоп-слово "badword", то DECLINE, иначе APPROVE
            var stopWords = new[] { "badword", "spam", "offensive" };
            foreach (var word in stopWords)
            {
                if (content.Contains(word, StringComparison.OrdinalIgnoreCase))
                    return PostState.DECLINE;
            }
            return PostState.APPROVE;
        }

        public override void Dispose()
        {
            _consumer?.Dispose();
            base.Dispose();
        }

        private class PostMessage
        {
            public long Id { get; set; }
            public long NewsId { get; set; }
            public string Content { get; set; } = string.Empty;
            public string Country { get; set; } = string.Empty;
            public string State { get; set; } = "PENDING";
        }
    }
}