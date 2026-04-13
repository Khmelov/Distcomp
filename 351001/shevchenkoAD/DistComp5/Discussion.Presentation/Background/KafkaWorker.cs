using System.Text.Json;
using Confluent.Kafka;
using Discussion.Application.Exceptions;
using Discussion.Application.Services.Interfaces;
using Shared.Constants;
using Shared.Enums;
using Shared.Messaging;

namespace Discussion.Presentation.Background;

public class KafkaWorker : BackgroundService
{
    private readonly IConsumer<string, string> _consumer;
    private readonly IProducer<string, string> _producer;
    private readonly IServiceProvider _serviceProvider; 

    public KafkaWorker(IConsumer<string, string> consumer, IProducer<string, string> producer, IServiceProvider serviceProvider)
    {
        _consumer = consumer;
        _producer = producer;
        _serviceProvider = serviceProvider;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        _consumer.Subscribe(KafkaTopics.InTopic);
        

        while (!stoppingToken.IsCancellationRequested)
        {
            var consumeResult = _consumer.Consume(stoppingToken);
            
            try
            {
                var request = JsonSerializer.Deserialize<KafkaRequest>(consumeResult.Message.Value);
                if (request == null) continue;

                
                using var scope = _serviceProvider.CreateScope();
                var commentService = scope.ServiceProvider.GetRequiredService<ICommentService>();

                KafkaResponse response;
                try
                {
                    response = await ProcessOperation(request, commentService);
                }
                catch (RestException ex) 
                {
                    response = new KafkaResponse 
                    { 
                        IsSuccess = false, 
                        ErrorMessage = ex.Message, 
                        ErrorSubCode = ex.SubCode,
                        CorrelationId = request.CorrelationId 
                    };
                }
                catch (Exception ex)
                {
                    response = new KafkaResponse 
                    { 
                        IsSuccess = false, 
                        ErrorMessage = "Internal Discussion Error: " + ex.Message, 
                        CorrelationId = request.CorrelationId 
                    };
                }

                
                if (!string.IsNullOrEmpty(request.CorrelationId))
                {
                    await _producer.ProduceAsync(KafkaTopics.OutTopic, new Message<string, string>
                    {
                        Key = consumeResult.Message.Key,
                        Value = JsonSerializer.Serialize(response)
                    });
                }
                
                
            }
            catch (Exception ex)
            {
                
            }
        }
    }

    private async Task<KafkaResponse> ProcessOperation(KafkaRequest request, ICommentService service)
    {
        switch (request.Method)
        {
            case "GET_ALL":
                var allComments = await service.GetAllAsync();
                return new KafkaResponse 
                { 
                    IsSuccess = true, 
                    PayloadList = allComments.ToList(), 
                    CorrelationId = request.CorrelationId 
                };
                
            case "CREATE":
                var created = await service.CreateAsync(request.Payload);
                return new KafkaResponse { IsSuccess = true, Payload = created, CorrelationId = request.CorrelationId };

            case "GET_BY_ID":
                var comment = await service.GetByIdAsync(request.Payload.Id ?? 0);
                return new KafkaResponse { IsSuccess = true, Payload = comment, CorrelationId = request.CorrelationId };

            case "GET_BY_ISSUE":
                var list = await service.GetByIssueIdAsync(request.Payload.IssueId);
                return new KafkaResponse { IsSuccess = true, PayloadList = list.ToList(), CorrelationId = request.CorrelationId };

            case "UPDATE":
                var updated = await service.UpdateAsync(request.Payload);
                return new KafkaResponse { IsSuccess = true, Payload = updated, CorrelationId = request.CorrelationId };
            
            case "DELETE":
                await service.DeleteAsync(request.Payload.Id ?? 0);
                return new KafkaResponse { IsSuccess = true, CorrelationId = request.CorrelationId };

            default:
                return new KafkaResponse { IsSuccess = false, ErrorMessage = "Unknown method", CorrelationId = request.CorrelationId };
        }
    }
}