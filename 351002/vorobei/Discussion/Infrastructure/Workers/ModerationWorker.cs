using System.Text.Json;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Servicies;
using Confluent.Kafka;
using DataAccess.Models;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Configuration;

namespace Infrastructure.Workers
{
    public enum MessageType { Create, Update, Get, GetAll, Delete }
    public class KafkaMessageEnvelope
    {
        public MessageType Type { get; set; }
        public string Payload { get; set; } = string.Empty;
    }

    public class ModerationWorker : BackgroundService
    {
        private readonly IServiceProvider _serviceProvider;
        private readonly IConsumer<string, string> _consumer;
        private readonly IProducer<string, string> _producer;
        private readonly string[] _stopWords = { "badword", "spam" };
        private readonly JsonSerializerOptions _jsonOptions = new() { PropertyNameCaseInsensitive = true };

        public ModerationWorker(IConfiguration config, IServiceProvider serviceProvider)
        {
            _serviceProvider = serviceProvider;
            var bootstrapServers = config["Kafka:BootstrapServers"];

            var consumerConfig = new ConsumerConfig
            {
                BootstrapServers = bootstrapServers,
                GroupId = "moderation-service-group",
                AutoOffsetReset = AutoOffsetReset.Earliest,
                EnableAutoCommit = true
            };
            _consumer = new ConsumerBuilder<string, string>(consumerConfig).Build();

            var producerConfig = new ProducerConfig
            {
                BootstrapServers = bootstrapServers,
                EnableIdempotence = true
            };
            _producer = new ProducerBuilder<string, string>(producerConfig).Build();
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            _consumer.Subscribe("InTopic");

            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    var result = _consumer.Consume(stoppingToken);
                    if (result?.Message?.Value == null || string.IsNullOrWhiteSpace(result.Message.Value))
                    {
                        continue;
                    }

                    KafkaMessageEnvelope? envelope;
                    try
                    {
                        envelope = JsonSerializer.Deserialize<KafkaMessageEnvelope>(result.Message.Value, _jsonOptions);
                    }
                    catch (JsonException)
                    {
                        Console.WriteLine($"[ModerationWorker] Invalid JSON received: {result.Message.Value}");
                        continue;
                    }

                    if (envelope == null) continue;

                    using var scope = _serviceProvider.CreateScope();
                    var postService = scope.ServiceProvider.GetRequiredService<IBaseService<PostRequestTo, PostResponseTo>>();

                    object? responseData = null;
                    string responseKey = result.Message.Key; // Сохраняем ключ для ответа

                    switch (envelope.Type)
                    {
                        case MessageType.Create:
                        case MessageType.Update:
                            var postDto = JsonSerializer.Deserialize<PostRequestTo>(envelope.Payload, _jsonOptions);
                            if (postDto != null)
                            {
                                // Логика модерации
                                postDto.State = _stopWords.Any(sw => postDto.Content.Contains(sw))
                                    ? PostState.DECLINE
                                    : PostState.APPROVE;

                                if (envelope.Type == MessageType.Create)
                                    responseData = await postService.CreateAsync(postDto);
                                else
                                    responseData = await postService.UpdateAsync(postDto);
                            }
                            break;

                        case MessageType.Get:
                            if (int.TryParse(envelope.Payload, out int id))
                                responseData = await postService.GetByIdAsync(id);
                            break;

                        case MessageType.GetAll:
                            responseData = await postService.GetAllAsync();
                            break;

                        case MessageType.Delete:
                            if (int.TryParse(envelope.Payload, out int delId))
                            {
                                await postService.DeleteByIdAsync(delId);
                                // Возвращаем пустой объект или ID как подтверждение удаления
                                responseData = new PostResponseTo { Id = delId };
                            }
                            break;
                    }

                    // 2. Если есть данные для ответа — отправляем в OutTopic
                    if (responseData != null)
                    {
                        var responseJson = JsonSerializer.Serialize(responseData);
                        await _producer.ProduceAsync("OutTopic", new Message<string, string>
                        {
                            Key = responseKey,
                            Value = responseJson
                        });
                    }
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"[ModerationWorker] Error: {ex.Message}");
                    await Task.Delay(1000, stoppingToken);
                }
            }
        }

        public override void Dispose()
        {
            _consumer.Close();
            _consumer.Dispose();
            _producer.Dispose();
            base.Dispose();
        }
    }
}