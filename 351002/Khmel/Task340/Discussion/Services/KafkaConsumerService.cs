using Confluent.Kafka;
using System.Text.Json;
using Discussion.DTOs;
using Discussion.Models;
using Discussion.Repositories;

namespace Discussion.Services
{
    public class KafkaConsumerService : BackgroundService
    {
        private readonly ILogger<KafkaConsumerService> _logger;
        private readonly IConsumer<string, string> _consumer;
        private readonly IServiceProvider _serviceProvider;
        private readonly IKafkaProducerService _kafkaProducer;
        private readonly IModerationService _moderationService;
        private readonly string _inTopic;

        public KafkaConsumerService(
            IConfiguration configuration,
            ILogger<KafkaConsumerService> logger,
            IServiceProvider serviceProvider,
            IKafkaProducerService kafkaProducer,
            IModerationService moderationService)
        {
            _logger = logger;
            _serviceProvider = serviceProvider;
            _kafkaProducer = kafkaProducer;
            _moderationService = moderationService;
            _inTopic = configuration["Kafka:InTopic"] ?? "InTopic";

            var config = new ConsumerConfig
            {
                BootstrapServers = configuration["Kafka:BootstrapServers"] ?? "localhost:9092",
                GroupId = "discussion-consumer-group",
                AutoOffsetReset = AutoOffsetReset.Earliest,
                EnableAutoCommit = false
            };

            _consumer = new ConsumerBuilder<string, string>(config).Build();
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            _consumer.Subscribe(_inTopic);
            _logger.LogInformation("Subscribed to InTopic");

            try
            {
                while (!stoppingToken.IsCancellationRequested)
                {
                    try
                    {
                        var consumeResult = _consumer.Consume(TimeSpan.FromMilliseconds(100));
                        
                        if (consumeResult != null)
                        {
                            await ProcessMessage(consumeResult.Message.Value);
                            _consumer.Commit(consumeResult);
                        }
                    }
                    catch (ConsumeException ex)
                    {
                        _logger.LogError(ex, "Error consuming message");
                    }
                    catch (Exception ex)
                    {
                        _logger.LogError(ex, "Error processing message");
                    }

                    await Task.Delay(10, stoppingToken);
                }
            }
            finally
            {
                _consumer.Close();
            }
        }

        private async Task ProcessMessage(string messageValue)
        {
            var message = JsonSerializer.Deserialize<KafkaCommentMessage>(messageValue);
            
            if (message?.Data == null)
            {
                _logger.LogWarning("Received null or invalid message");
                return;
            }

            _logger.LogInformation(
                "Processing {Action} for comment {Id}",
                message.Action, message.Data.Id);

            using var scope = _serviceProvider.CreateScope();
            var repository = scope.ServiceProvider.GetRequiredService<ICommentRepository>();

            switch (message.Action.ToUpper())
            {
                case "CREATE":
                    await HandleCreate(message.Data, repository);
                    break;
                case "UPDATE":
                    await HandleUpdate(message.Data, repository);
                    break;
                case "DELETE":
                    await HandleDelete(message.Data, repository);
                    break;
                default:
                    _logger.LogWarning("Unknown action: {Action}", message.Action);
                    break;
            }
        }

        private async Task HandleCreate(CommentData data, ICommentRepository repository)
        {
            var moderationResult = _moderationService.ModerateContent(data.Content);

            var comment = new Comment
            {
                Id = data.Id,
                StoryId = data.StoryId,
                Content = data.Content,
                Country = data.Country,
                State = moderationResult,
                Created = DateTimeOffset.UtcNow
            };

            await repository.CreateAsync(comment);

            var responseMessage = new KafkaCommentMessage
            {
                Action = "CREATED",
                Data = new CommentData
                {
                    Id = comment.Id,
                    StoryId = comment.StoryId,
                    Content = comment.Content,
                    Country = comment.Country,
                    State = comment.State
                }
            };

            await _kafkaProducer.SendAsync("OutTopic", responseMessage);

            _logger.LogInformation(
                "Comment {Id} created with state {State}",
                comment.Id, comment.State);
        }

        private async Task HandleUpdate(CommentData data, ICommentRepository repository)
        {
            var existing = await repository.GetByIdAsync(data.StoryId, data.Id);
            if (existing == null)
            {
                _logger.LogWarning("Comment {Id} not found for update", data.Id);
                return;
            }

            var moderationResult = _moderationService.ModerateContent(data.Content);

            var comment = new Comment
            {
                Id = data.Id,
                StoryId = data.StoryId,
                Content = data.Content,
                Country = data.Country,
                State = moderationResult,
                Created = existing.Created
            };

            await repository.UpdateAsync(comment);

            var responseMessage = new KafkaCommentMessage
            {
                Action = "UPDATED",
                Data = new CommentData
                {
                    Id = comment.Id,
                    StoryId = comment.StoryId,
                    Content = comment.Content,
                    Country = comment.Country,
                    State = comment.State
                }
            };

            await _kafkaProducer.SendAsync("OutTopic", responseMessage);
        }

        private async Task HandleDelete(CommentData data, ICommentRepository repository)
        {
            await repository.DeleteAsync(data.StoryId, data.Id);

            var responseMessage = new KafkaCommentMessage
            {
                Action = "DELETED",
                Data = new CommentData
                {
                    Id = data.Id,
                    StoryId = data.StoryId
                }
            };

            await _kafkaProducer.SendAsync("OutTopic", responseMessage);

            _logger.LogInformation("Comment {Id} deleted", data.Id);
        }

        public override void Dispose()
        {
            _consumer?.Dispose();
            base.Dispose();
        }
    }
}