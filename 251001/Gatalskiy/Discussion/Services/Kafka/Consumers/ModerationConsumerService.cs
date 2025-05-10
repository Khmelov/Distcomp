using System.Text.Json;
using Confluent.Kafka;
using Discussion.DTOs;

namespace Discussion.Services.Kafka.Consumers;

public class KafkaDiscussionConsumerService : BackgroundService
{
    private readonly IConsumer<Ignore, string> _consumer;
    private readonly IProducer<string, string> _producer;
    private readonly string _inTopic = "InTopic";
    private readonly string _outTopic = "OutTopic";
    private readonly INoticeService _noticeService;
    private readonly ILogger<KafkaDiscussionConsumerService> _logger;

    public KafkaDiscussionConsumerService(
        INoticeService noticeService,
        ILogger<KafkaDiscussionConsumerService> logger)
    {
        _noticeService = noticeService;
        _logger = logger;

        var consumerConfig = new ConsumerConfig
        {
            BootstrapServers = "localhost:29092",
            GroupId = "discussion-group",
            AutoOffsetReset = AutoOffsetReset.Earliest,
            EnableAutoCommit = false,
            AllowAutoCreateTopics = true
        };
        
        _consumer = new ConsumerBuilder<Ignore, string>(consumerConfig)
            .SetErrorHandler((_, e) => logger.LogError($"Kafka error: {e.Reason}"))
            .Build();

        var producerConfig = new ProducerConfig
        {
            BootstrapServers = "localhost:29092",
            MessageTimeoutMs = 5000
        };
        
        _producer = new ProducerBuilder<string, string>(producerConfig).Build();
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        await Task.Yield(); // Освобождаем поток для запуска других сервисов
        
        _consumer.Subscribe(_inTopic);
        _logger.LogInformation("Kafka consumer started");

        try
        {
            while (!stoppingToken.IsCancellationRequested)
            {
                try
                {
                    var consumeResult = _consumer.Consume(stoppingToken);
                    if (consumeResult == null) continue;

                    // Обработка сообщения в отдельной задаче
                    _ = ProcessMessageAsync(consumeResult, stoppingToken);
                }
                catch (ConsumeException e)
                {
                    _logger.LogError($"Consume error: {e.Error.Reason}");
                    if (e.Error.IsFatal) break;
                }
                catch (OperationCanceledException)
                {
                    break;
                }
                catch (Exception e)
                {
                    _logger.LogError($"Unexpected error: {e.Message}");
                    await Task.Delay(1000, stoppingToken);
                }
            }
        }
        finally
        {
            _logger.LogInformation("Closing Kafka consumer");
            _consumer.Close();
            _consumer.Dispose();
            _producer.Dispose();
        }
    }

    private async Task ProcessMessageAsync(ConsumeResult<Ignore, string> consumeResult, CancellationToken ct)
    {
        try
        {
            var messageRes = consumeResult.Message;
            var correlationIdHeader = messageRes.Headers.FirstOrDefault(x => x.Key == "correlation-id");
            var notice = JsonSerializer.Deserialize<NoticeResponseDTO>(consumeResult.Message.Value);

            _logger.LogInformation($"Processing notice: {notice.Id}");

            // Ваш оригинальный switch-блок
             switch (notice.OperationType)
                {
                    case OperationType.Create:
                    {
                        NoticeRequestDTO noticeRequestDto = new NoticeRequestDTO();
                            noticeRequestDto.Id = notice.Id;
                            noticeRequestDto.Content = notice.Content;
                            noticeRequestDto.NewsId = notice.NewsId;
                        var response = await _noticeService.CreateNoticeAsync(noticeRequestDto);
                        response.OperationType = OperationType.Create;
                        var message = new Message<string, string>
                        {
                            Key = response.NewsId.ToString() ?? "default",
                            Value = JsonSerializer.Serialize(response),
                            Headers = new Headers
                            {
                                new Header( "correlation-id",correlationIdHeader.GetValueBytes())   
                            }
                        };
                        await _producer.ProduceAsync(_outTopic, message);
                        _logger.LogInformation($"SENT CREATE RESPONSE.");
                        break;
                    }
                    case OperationType.Update:
                    {
                        NoticeRequestDTO noticeUpdateDto = new NoticeRequestDTO();
                        noticeUpdateDto.Id = notice.Id;
                        noticeUpdateDto.Content = notice.Content;
                        noticeUpdateDto.NewsId = notice.NewsId;
                        var response =await  _noticeService.UpdateNoticeAsync(notice.Id,noticeUpdateDto);
                        response.OperationType = OperationType.Update;
                        var message = new Message<string, string>
                        {
                            Key = notice.Id.ToString() ?? "default",
                            Value = JsonSerializer.Serialize(response),
                            Headers = new Headers
                            {
                                new Header( "correlation-id",correlationIdHeader.GetValueBytes())   
                            }
                        };
                        await _producer.ProduceAsync(_outTopic, message);
                        _logger.LogInformation($"SENT Update RESPONSE.");
                        break;
                    }
                    case OperationType.Delete:
                    {
                        await _noticeService.DeleteNoticeAsync(notice.Id);
                        var response = new NoticeResponseDTO();
                        response.OperationType = OperationType.Delete;
                        var message = new Message<string, string>
                        {
                            Key = notice.Id.ToString() ?? "default",
                            Value = JsonSerializer.Serialize(response),
                            Headers = new Headers
                            {
                                new Header( "correlation-id",correlationIdHeader.GetValueBytes())   
                            }
                        };
                        await _producer.ProduceAsync(_outTopic, message);
                        _logger.LogInformation($"SENT DELETE RESPONSE.");
                        break;
                    }
                    case OperationType.GetAll:
                    {
                        var response = await _noticeService.GetAllNoticesAsync();
                        var message = new Message<string, string>
                        {
                            Key = notice.Id.ToString() ?? "default",
                            Value = JsonSerializer.Serialize(response),
                            Headers = new Headers
                            {
                                new Header( "correlation-id",correlationIdHeader.GetValueBytes())   
                            }
                        };
                        await _producer.ProduceAsync(_outTopic, message);
                        _logger.LogInformation($"SENT GETALL RESPONSE.");
                        break;
                    }
                    case OperationType.GetById:
                    {
                        var response = await _noticeService.GetNoticeByIdAsync(notice.Id);
                        response.OperationType = OperationType.GetById;
                        var message = new Message<string, string>
                        {
                            Key = notice.Id.ToString() ?? "default",
                            Value = JsonSerializer.Serialize(response),
                            Headers = new Headers
                            {
                                new Header( "correlation-id",correlationIdHeader.GetValueBytes())   
                            }
                        };
                        await _producer.ProduceAsync(_outTopic, message);
                        _logger.LogInformation($"SENT GETBYID RESPONSE.");
                        break;
                    }
                    
                }
        }
        catch (Exception e)
        {
            _logger.LogError($"Error processing message: {e.Message}");
        }
    }

    public override async Task StopAsync(CancellationToken cancellationToken)
    {
        _logger.LogInformation("Stopping Kafka consumer service");
        await base.StopAsync(cancellationToken);
    }
}