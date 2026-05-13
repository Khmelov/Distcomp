using System.Text.Json;
using BusinessLogic.DTO.Response;
using Confluent.Kafka;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;

namespace Infrastructure.Kafka
{
    public class PostModerationResultConsumer : BackgroundService
    {
        private readonly IConsumer<string, string> _consumer;
        private readonly IModerationResultWaiter _resultWaiter;
        private readonly string _outTopic = "OutTopic";
        private readonly JsonSerializerOptions _jsonOptions = new() { PropertyNameCaseInsensitive = true };

        public PostModerationResultConsumer(
            IConfiguration config,
            IModerationResultWaiter resultWaiter)
        {
            _resultWaiter = resultWaiter;

            var consumerConfig = new ConsumerConfig
            {
                BootstrapServers = config["Kafka:BootstrapServers"],
                // Важно: если у вас несколько запущенных инстансов Publisher, 
                // каждый должен получать ВСЕ ответы, чтобы найти "свой" TaskCompletionSource.
                // В таком случае GroupId должен быть уникальным для каждого инстанса.
                GroupId = $"publisher-group-{Guid.NewGuid()}",
                AutoOffsetReset = AutoOffsetReset.Latest, // Нам нужны только новые ответы
                EnableAutoCommit = true
            };

            _consumer = new ConsumerBuilder<string, string>(consumerConfig).Build();
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            _consumer.Subscribe(_outTopic);

            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    // Ожидаем сообщение (блоковый метод, управляется stoppingToken)
                    var consumeResult = _consumer.Consume(stoppingToken);
                    if (consumeResult?.Message == null) continue;

                    var messageKey = consumeResult.Message.Key;
                    var messageValue = consumeResult.Message.Value;

                    // 1. Проверяем, не список ли это (для GetAll)
                    if (messageKey == "all_query")
                    {
                        var postList = JsonSerializer.Deserialize<List<PostResponseTo>>(messageValue, _jsonOptions);
                        if (postList != null)
                        {
                            _resultWaiter.SetListResult(messageKey, postList);
                            Console.WriteLine($"[Consumer] Delivered list result for: {messageKey}");
                        }
                    }
                    // 2. Иначе обрабатываем как одиночный объект (Get, Update, Delete)
                    else
                    {
                        var postResponse = JsonSerializer.Deserialize<PostResponseTo>(messageValue, _jsonOptions);
                        if (postResponse != null)
                        {
                            // Разблокируем поток в PostService, который ждет этот ID
                            _resultWaiter.SetResult(postResponse.Id, postResponse);
                            Console.WriteLine($"[Consumer] Delivered result for ID: {postResponse.Id}");
                        }
                        else if (int.TryParse(messageKey, out int id))
                        {
                            // Случай для Delete, если Consumer возвращает просто подтверждение/ID
                            _resultWaiter.SetResult(id, new PostResponseTo { Id = id });
                        }
                    }
                }
                catch (OperationCanceledException)
                {
                    break;
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"[Consumer] Critical Error: {ex.Message}");
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