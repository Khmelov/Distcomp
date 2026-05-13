using System.Text.Json;
using Confluent.Kafka;
using Discussion.src.NewsPortal.Discussion.Application.Dtos.RequestTo;
using Discussion.src.NewsPortal.Discussion.Application.Services.Abstractions;
using Discussion.src.NewsPortal.Discussion.Domain.Entities;
using Discussion.src.NewsPortal.Discussion.Domain.Exceptions;

namespace Discussion.src.NewsPortal.Discussion.Infrastructure.Messaging
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
            _logger.LogInformation("Discussion subscribed to {InTopic}", _inTopic);

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
            var message = JsonSerializer.Deserialize<KafkaNoteMessage>(messageValue);

            if (message?.Data == null)
            {
                _logger.LogWarning("Received null or invalid message");
                return;
            }

            _logger.LogInformation("Processing {Action} for note {Id}", message.Action, message.Data.Id);

            using var scope = _serviceProvider.CreateScope();
            var noteService = scope.ServiceProvider.GetRequiredService<INoteService>();

            switch (message.Action.ToUpper())
            {
                case "CREATE":
                    await HandleCreate(message.Data, noteService);
                    break;
                case "UPDATE":
                    await HandleUpdate(message.Data, noteService);
                    break;
                case "DELETE":
                    await HandleDelete(message.Data, noteService);
                    break;
                case "GET":
                    await HandleGet(message.Data, noteService);
                    break;
                case "GET_ALL":
                    await HandleGetAll(noteService);
                    break;
                default:
                    _logger.LogWarning("Unknown action: {Action}", message.Action);
                    break;
            }
        }

        private async Task HandleCreate(Note data, INoteService noteService)
        {
            try
            {
                await noteService.ValidateNewsExistsAsync(data.NewsId);

                var moderationResult = _moderationService.ModerateContent(data.Content);

                var createRequest = new NoteRequestTo
                {
                    Id = data.Id, 
                    NewsId = data.NewsId,
                    Content = data.Content,
                    State = moderationResult
                };

                var createdNoteResponse = await noteService.CreateNoteAsync(createRequest);
            }
            catch (NotFoundException ex)
            {
                _logger.LogWarning(ex, "News {NewsId} not found, cannot create note {Id}", data.NewsId, data.Id);
            }
        }

        private async Task HandleUpdate(Note data, INoteService noteService)
        {
            try
            {
                var existing = await noteService.GetNoteByIdAsync(data.Id);
                if (existing == null)
                {
                    _logger.LogWarning("Note {Id} not found for update", data.Id);
                    return;
                }

                await noteService.ValidateNewsExistsAsync(data.NewsId);

                var moderationResult = _moderationService.ModerateContent(data.Content);

                _logger.LogInformation("Note {Id} update moderation result: {State}", data.Id, moderationResult);

                var updateRequest = new NoteRequestTo
                {
                    Id = data.Id,
                    NewsId = data.NewsId,
                    Content = data.Content,
                    State = moderationResult  
                };

                await noteService.UpdateNoteAsync(updateRequest);

                var updatedNote = await noteService.GetNoteByIdAsync(data.Id);

                var responseMessage = new KafkaNoteMessage
                {
                    Action = "UPDATED",
                    Data = new Note
                    {
                        Id = updatedNote.Id,
                        NewsId = updatedNote.NewsId,
                        Content = updatedNote.Content,
                        State = updatedNote.State
                    }
                };

                await _kafkaProducer.SendAsync("OutTopic", responseMessage);

                _logger.LogInformation("Note {Id} updated with state {State}", updatedNote.Id, updatedNote.State);
            }
            catch (NotFoundException ex)
            {
                _logger.LogWarning(ex, "News {NewsId} not found, cannot create note {Id}", data.NewsId, data.Id);
            }
        }

        private async Task HandleDelete(Note data, INoteService noteService)
        {
            try
            {
                var existing = await noteService.GetNoteByIdAsync(data.Id);
                if (existing == null)
                {
                    _logger.LogWarning("Note {Id} not found for deletion", data.Id);
                    return;
                }

                await noteService.DeleteNoteAsync(data.Id);

                var responseMessage = new KafkaNoteMessage
                {
                    Action = "DELETED",
                    Data = new Note
                    {
                        Id = data.Id,
                        NewsId = data.NewsId,
                        State = "DELETED"
                    }
                };

                await _kafkaProducer.SendAsync("OutTopic", responseMessage);

                _logger.LogInformation("Note {Id} deleted", data.Id);
            }
            catch (NotFoundException)
            {
                _logger.LogWarning("Note {Id} not found for deletion", data.Id);
            }
        }

        private async Task HandleGet(Note data, INoteService noteService)
        {
            try
            {
                var note = await noteService.GetNoteByIdAsync(data.Id);

                if (note == null)
                {
                    _logger.LogWarning("Note {Id} not found for GET request", data.Id);

                    var errorMessage = new KafkaNoteMessage
                    {
                        Action = "NOT_FOUND",
                        Data = new Note
                        {
                            Id = data.Id,
                            State = "NOT_FOUND",
                            Content = $"Note with ID {data.Id} not found"
                        }
                    };
                    await _kafkaProducer.SendAsync("OutTopic", errorMessage);
                    return;
                }

                var responseMessage = new KafkaNoteMessage
                {
                    Action = "FOUND",
                    Data = new Note
                    {
                        Id = note.Id,
                        NewsId = note.NewsId,
                        Content = note.Content,
                        State = note.State
                    }
                };

                await _kafkaProducer.SendAsync("OutTopic", responseMessage);
                _logger.LogInformation("Note {Id} retrieved via Kafka", note.Id);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting note {Id} via Kafka", data.Id);

                var errorMessage = new KafkaNoteMessage
                {
                    Action = "ERROR",
                    Data = new Note
                    {
                        Id = data.Id,
                        State = "ERROR",
                        Content = ex.Message
                    }
                };
                await _kafkaProducer.SendAsync("OutTopic", errorMessage);
            }
        }

        private async Task HandleGetAll(INoteService noteService)
        {
            try
            {
                var notes = await noteService.GetAllNotesAsync();
                var notesList = notes.ToList();

                _logger.LogInformation("Retrieved {Count} notes via Kafka GET_ALL", notesList.Count);

                // Отправляем все заметки одной партией
                var responseMessage = new KafkaNoteMessage
                {
                    Action = "ALL_NOTES",
                    Data = new Note
                    {
                        Id = 0,
                        NewsId = 0,
                        Content = JsonSerializer.Serialize(notesList),
                        State = "SUCCESS"
                    }
                };

                await _kafkaProducer.SendAsync("OutTopic", responseMessage);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting all notes via Kafka");

                var errorMessage = new KafkaNoteMessage
                {
                    Action = "ERROR",
                    Data = new Note
                    {
                        Id = 0,
                        State = "ERROR",
                        Content = ex.Message
                    }
                };
                await _kafkaProducer.SendAsync("OutTopic", errorMessage);
            }
        }

        public override void Dispose()
        {
            _consumer?.Dispose();
            base.Dispose();
        }
    }
}