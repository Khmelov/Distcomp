using System.Text.Json;
using Confluent.Kafka;
using DiscussionService.Domain.Entities;
using Microsoft.Extensions.Hosting;
using Shared.Domain.DTOs;
using Shared.Domain.Interfaces;
using System.Diagnostics;

namespace DiscussionService.Infrastructure.Kafka;

public class DiscussionWorker : BackgroundService
{
    private readonly IRepository<long, Comment<long>> _repository;
    private readonly string _bootstrapServers = "127.0.0.1:9092"; // Используем IP вместо localhost для скорости

    public DiscussionWorker(IRepository<long, Comment<long>> repository)
    {
        _repository = repository;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        // 1. Настройка консьюмера для минимальной задержки
        var consumerConfig = new ConsumerConfig
        {
            BootstrapServers = _bootstrapServers,
            GroupId = $"discussion-group-{Guid.NewGuid()}", // Новый ID для сброса оффсетов при тестах
            AutoOffsetReset = AutoOffsetReset.Latest,
            FetchWaitMaxMs = 5,       // Опрос каждые 5мс
            FetchMinBytes = 1,        // Получать данные мгновенно
            SocketNagleDisable = true
        };

        // 2. Настройка продюсера для мгновенной отправки (без буферизации)
        var producerConfig = new ProducerConfig
        {
            BootstrapServers = _bootstrapServers,
            LingerMs = 0,             // Отправлять сообщение немедленно, не ждать пачки
            Acks = Acks.Leader        // Ждем подтверждения только от лидера (быстрее)
        };

        using var consumer = new ConsumerBuilder<string, string>(consumerConfig).Build();
        using var producer = new ProducerBuilder<string, string>(producerConfig).Build();

        consumer.Subscribe("InTopic");
        Console.WriteLine("[DiscussionWorker] Подписка оформлена, слушаю...");

        var jsonOptions = new JsonSerializerOptions { PropertyNameCaseInsensitive = true };

        while (!stoppingToken.IsCancellationRequested)
        {
            try
            {
                // Используем таймаут, чтобы цикл не зависал навсегда, если сообщений нет
                var consumeResult = consumer.Consume(TimeSpan.FromMilliseconds(100));
                
                if (consumeResult == null) continue;

                Console.WriteLine($"[DiscussionWorker] ПОЛУЧЕНО: {consumeResult.Message.Value}");

                var comment = JsonSerializer.Deserialize<CommentKafkaMessage>(consumeResult.Message.Value, jsonOptions);
                if (comment == null) continue;

                // Простая "модерация"
                comment.State = comment.Text.Contains("плохое") ? "DECLINE" : "APPROVE";

                var entity = new Comment<long>
                {
                    ID = comment.ID,
                    StoryID = comment.StoryID,
                    Content = comment.Text
                };

                // Сохранение в Cassandra
                await _repository.AddAsync(entity);

                // Отправка ответа в OutTopic
                var responseJson = JsonSerializer.Serialize(comment);
                await producer.ProduceAsync("OutTopic", new Message<string, string>
                {
                    Key = consumeResult.Message.Key,
                    Value = responseJson
                });
                
                // Принудительно выталкиваем сообщение из буфера продюсера
                producer.Flush(stoppingToken); 
            }
            catch (ConsumeException e) when (e.Error.Code == ErrorCode.UnknownTopicOrPart)
            {
                await Task.Delay(500, stoppingToken); // Уменьшили ожидание с 5000 до 500
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[DiscussionWorker] ОШИБКА: {ex.Message}");
                await Task.Delay(100, stoppingToken);
            }
        }
    }
}