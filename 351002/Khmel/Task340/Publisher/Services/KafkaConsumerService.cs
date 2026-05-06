using Confluent.Kafka;
using System.Text.Json;
using Publisher.DTOs;

namespace Publisher.Services
{
    public class KafkaConsumerService : BackgroundService
    {
        private readonly ILogger<KafkaConsumerService> _logger;
        private readonly IConsumer<string, string> _consumer;
        private readonly string _topic;

        private static readonly Dictionary<long, CommentResponseDto> _commentCache = new();
        private static readonly object _lock = new();

        public KafkaConsumerService(IConfiguration configuration, ILogger<KafkaConsumerService> logger)
        {
            _logger = logger;
            _topic = configuration["Kafka:OutTopic"] ?? "OutTopic";

            var config = new ConsumerConfig
            {
                BootstrapServers = configuration["Kafka:BootstrapServers"] ?? "localhost:9092",
                GroupId = "publisher-consumer-group",
                AutoOffsetReset = AutoOffsetReset.Earliest,
                EnableAutoCommit = false
            };

            _consumer = new ConsumerBuilder<string, string>(config).Build();
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            _consumer.Subscribe(_topic);
            _logger.LogInformation("Subscribed to OutTopic");

            try
            {
                while (!stoppingToken.IsCancellationRequested)
                {
                    try
                    {
                        var consumeResult = _consumer.Consume(TimeSpan.FromMilliseconds(100));
                        
                        if (consumeResult != null)
                        {
                            var message = JsonSerializer.Deserialize<KafkaCommentMessage>(consumeResult.Message.Value);
                            
                            if (message?.Data != null)
                            {
                                var response = new CommentResponseDto
                                {
                                    Id = message.Data.Id,
                                    StoryId = message.Data.StoryId,
                                    Content = message.Data.Content,
                                    Country = message.Data.Country,
                                    State = message.Data.State
                                };

                                lock (_lock)
                                {
                                    _commentCache[response.Id] = response;
                                }

                                _logger.LogInformation(
                                    "Received comment {Id} with state {State} from OutTopic",
                                    response.Id, response.State);
                            }

                            _consumer.Commit(consumeResult);
                        }
                    }
                    catch (ConsumeException ex)
                    {
                        _logger.LogError(ex, "Error consuming message");
                    }

                    await Task.Delay(10, stoppingToken);
                }
            }
            finally
            {
                _consumer.Close();
            }
        }

        public static void AddComment(CommentResponseDto comment)
        {
            lock (_lock)
            {
                _commentCache[comment.Id] = comment;
            }
        }

        public static CommentResponseDto? GetComment(long id)
        {
            lock (_lock)
            {
                return _commentCache.TryGetValue(id, out var comment) ? comment : null;
            }
        }

        public static IEnumerable<CommentResponseDto> GetAllComments()
        {
            lock (_lock)
            {
                return _commentCache.Values.ToList();
            }
        }

        public override void Dispose()
        {
            _consumer?.Dispose();
            base.Dispose();
        }
    }
}