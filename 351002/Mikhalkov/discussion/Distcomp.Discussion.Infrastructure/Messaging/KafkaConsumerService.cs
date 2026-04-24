using Confluent.Kafka;
using Distcomp.Discussion.Infrastructure.Repositories;
using Distcomp.Shared.Models;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.DependencyInjection;
using System.Text.Json;

namespace Distcomp.Discussion.Infrastructure.Messaging
{
    public class KafkaConsumerService : BackgroundService
    {
        private readonly IServiceProvider _serviceProvider;
        private readonly IConfiguration _config;
        private readonly IConsumer<string, string> _consumer;
        private readonly IProducer<string, string> _replyProducer;

        public KafkaConsumerService(IServiceProvider serviceProvider, IConfiguration config)
        {
            _serviceProvider = serviceProvider;
            _config = config;

            var bootstrapServers = _config["Kafka:BootstrapServers"] ?? "localhost:9092";

            var consumerConfig = new ConsumerConfig
            {
                BootstrapServers = bootstrapServers,
                GroupId = "discussion-group",
                AutoOffsetReset = AutoOffsetReset.Earliest
            };
            _consumer = new ConsumerBuilder<string, string>(consumerConfig).Build();

            var producerConfig = new ProducerConfig { BootstrapServers = bootstrapServers };
            _replyProducer = new ProducerBuilder<string, string>(producerConfig).Build();
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            _consumer.Subscribe("InTopic");
            await Task.Yield();

            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    var consumeResult = _consumer.Consume(stoppingToken);
                    if (consumeResult != null && consumeResult.Message != null)
                    {
                        await ProcessMessage(consumeResult.Message.Value);
                    }
                }
                catch (OperationCanceledException) { break; }
                catch (Exception ex)
                {
                    Console.WriteLine($"[Kafka Consumer Error]: {ex.Message}");
                }
            }
        }

        private async Task ProcessMessage(string json)
        {
            var request = JsonSerializer.Deserialize<NoteOperationMessage>(json);
            if (request == null) return;

            using var scope = _serviceProvider.CreateScope();
            var repo = scope.ServiceProvider.GetRequiredService<NoteRepository>();

            object? resultData = null;

            try
            {
                switch (request.Operation)
                {
                    case NoteOperation.CREATE:
                        var newNote = request.Note!;                     
                        newNote.State = newNote.Content.ToLower().Contains("bad") ? NoteState.DECLINE : NoteState.APPROVE;
                        repo.Save(newNote);
                        resultData = newNote;
                        break;

                    case NoteOperation.GET_BY_ID:
                        resultData = repo.GetByIdOnly(request.NoteId ?? 0);
                        break;

                    case NoteOperation.GET_ALL:
                        resultData = repo.GetAll().ToList();
                        break;

                    case NoteOperation.UPDATE:
                        var existingNote = repo.GetByIdOnly(request.NoteId ?? 0);
                        if (existingNote != null)
                        {
                            existingNote.Content = request.Note!.Content;
                            repo.Save(existingNote);
                            resultData = existingNote; 
                        }
                        else { resultData = null; }
                        break;

                    case NoteOperation.DELETE:
                        var existing = repo.GetByIdOnly(request.NoteId ?? 0);
                        if (existing != null)
                        {
                            repo.Delete(existing.Country, existing.IssueId, existing.Id);
                            resultData = new { Success = true };
                        }
                        else { resultData = null; }
                        break;
                }
            }
            catch (Exception ex)
            {
                resultData = new { Error = ex.Message };
            }

            var jsonOptions = new JsonSerializerOptions
            {
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase
            };

            var response = new
            {
                CorrelationId = request.CorrelationId,
                Data = resultData
            };

            var responseJson = JsonSerializer.Serialize(response, jsonOptions);
            await _replyProducer.ProduceAsync("OutTopic", new Message<string, string>
            {
                Key = request.CorrelationId,
                Value = responseJson
            });

            Console.WriteLine($"[Kafka] Handled {request.Operation} for CorrelationId: {request.CorrelationId}");
        }

        public override void Dispose()
        {
            _consumer.Close();
            _consumer.Dispose();
            _replyProducer.Dispose();
            base.Dispose();
        }
    }
}