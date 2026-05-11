using Confluent.Kafka;
using System.Text.Json;
using DiscussionModule.interfaces;
using DiscussionModule.DTOs.requests;
using AutoMapper;

namespace DiscussionModule.kafka;

public class KafkaConsumer : BackgroundService
{
    private readonly IConsumer<string, string> _consumer;
    private readonly IServiceProvider _serviceProvider;
    private readonly string _inTopic;
    private readonly KafkaProducer _producer;
    private readonly List<string> _stopWords = new() { "spam", "bad", "offensive", "xxx" };

    public KafkaConsumer(IServiceProvider serviceProvider, string bootstrapServers, string inTopic, string outTopic)
    {
        var config = new ConsumerConfig
        {
            BootstrapServers = bootstrapServers,
            GroupId = "discussion-consumer-group",
            AutoOffsetReset = AutoOffsetReset.Earliest,
            EnableAutoCommit = true
        };

        _consumer = new ConsumerBuilder<string, string>(config).Build();
        _serviceProvider = serviceProvider;
        _inTopic = inTopic;
        _producer = new KafkaProducer(bootstrapServers, outTopic);
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        _consumer.Subscribe(_inTopic);
        Console.WriteLine($"[Kafka] Consumer started, listening to topic: {_inTopic}");

        while (!stoppingToken.IsCancellationRequested)
        {
            try
            {
                var consumeResult = _consumer.Consume(stoppingToken);
                
                if (consumeResult != null)
                {
                    await ProcessMessageAsync(consumeResult.Message.Key, consumeResult.Message.Value);
                }
            }
            catch (OperationCanceledException)
            {
                break;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[Kafka] Error: {ex.Message}");
            }
        }

        _consumer.Close();
    }

    private async Task ProcessMessageAsync(string key, string jsonMessage)
    {
        using var scope = _serviceProvider.CreateScope();
        var noteService = scope.ServiceProvider.GetRequiredService<INoteService>();
        var mapper = scope.ServiceProvider.GetRequiredService<IMapper>();
        var operation = key;
        var noteRequest = JsonSerializer.Deserialize<NoteRequestTo>(jsonMessage);
        
        try
        {
            object response = null;
            string status = "SUCCESS";

            switch (operation.ToUpper())
            {
                case "CREATE":
                    if (await ModerateNoteAsync(noteRequest))
                    {
                        var createdNote = await noteService.CreateNote(noteRequest);
                        response = createdNote;
                        status = "APPROVED";
                    }
                    else
                    {
                        status = "DECLINED";
                        response = new { Message = "Note declined by moderation" };
                    }
                    break;

                case "GET":
                    response = await noteService.GetNote(noteRequest);
                    break;

                case "UPDATE":
                    response = await noteService.UpdateNote(noteRequest);
                    break;

                case "DELETE":
                    await noteService.DeleteNote(noteRequest);
                    response = new { Message = "Deleted successfully" };
                    break;
            }

            var responseMessage = new
            {
                Operation = operation,
                Status = status,
                Data = response,
                RequestId = noteRequest?.Id ?? 0
            };

            await _producer.SendMessageAsync(noteRequest?.Id.ToString() ?? "0", responseMessage);
        }
        catch (Exception ex)
        {
            var errorResponse = new
            {
                Operation = key,
                Status = "ERROR",
                Error = ex.Message,
                RequestId = noteRequest?.Id ?? 0
            };

            await _producer.SendMessageAsync(noteRequest?.Id.ToString() ?? "0", errorResponse);
            Console.WriteLine($"[Kafka] Error processing: {ex.Message}");
        }
    }

    private Task<bool> ModerateNoteAsync(NoteRequestTo note)
    {

        // Проверка стоп-слов
        foreach (var stopWord in _stopWords)
        {
            if (note.Content.ToLower().Contains(stopWord))
            {
                return Task.FromResult(false);
            }
        }

        return Task.FromResult(true);
    }

    public override void Dispose()
    {
        _consumer?.Dispose();
        _producer?.Dispose();
        base.Dispose();
    }
}