using System.Text.Json;
using Confluent.Kafka;
using discussion.Kafka;
using discussion.Models.DTO.Requests;
using discussion.Services;
using Microsoft.Extensions.Options;

namespace discussion.Infrastructure;

/// <summary>Читает InTopic и обрабатывает команды модулем discussion (Kafka → Cassandra).</summary>
public sealed class DiscussionInTopicConsumerHostedService : BackgroundService
{
    private readonly IServiceScopeFactory _scopeFactory;
    private readonly IProducer<string, string> _producer;
    private readonly KafkaOptions _opt;
    private readonly ILogger<DiscussionInTopicConsumerHostedService> _logger;

    public DiscussionInTopicConsumerHostedService(
        IServiceScopeFactory scopeFactory,
        IProducer<string, string> kafkaProducer,
        IOptions<KafkaOptions> kafkaOptions,
        ILogger<DiscussionInTopicConsumerHostedService> logger)
    {
        _scopeFactory = scopeFactory;
        _producer = kafkaProducer;
        _opt = kafkaOptions.Value;
        _logger = logger;
    }

    protected override async Task ExecuteAsync(CancellationToken stoppingToken)
    {
        await Task.Yield();

        var cfg = new ConsumerConfig
        {
            BootstrapServers = _opt.BootstrapServers,
            GroupId = _opt.DiscussionConsumerGroup,
            AutoOffsetReset = AutoOffsetReset.Earliest,
            EnableAutoCommit = true,
            AllowAutoCreateTopics = true
        };

        using var consumer = new ConsumerBuilder<string, string>(cfg).Build();
        consumer.Subscribe(_opt.InTopic);
        _logger.LogInformation("Kafka consumer subscribed to {Topic}", _opt.InTopic);

        while (!stoppingToken.IsCancellationRequested)
        {
            ConsumeResult<string, string>? cr = null;
            try
            {
                cr = consumer.Consume(TimeSpan.FromSeconds(1));
            }
            catch (ConsumeException ex)
            {
                _logger.LogError(ex, "Kafka consume error");
            }

            if (cr?.Message.Value == null)
                continue;

            try
            {
                var envelope = JsonSerializer.Deserialize<NoticeInEnvelope>(cr.Message.Value, DiscussionKafkaJson.SerializerOptions);
                if (envelope?.Kind == null)
                    continue;
                await DispatchAsync(envelope, stoppingToken).ConfigureAwait(false);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Failed to handle inbound notice message");
            }
        }

        consumer.Close();
    }

    private async Task DispatchAsync(NoticeInEnvelope envelope, CancellationToken ct)
    {
        using var scope = _scopeFactory.CreateScope();
        var app = scope.ServiceProvider.GetRequiredService<INoticeAppService>();

        switch (envelope.Kind)
        {
            case NoticeKafkaKinds.CreateDraft:
                await app.CreateWithAssignedIdAsync(
                        envelope.Id,
                        new NoticeRequestTo { Id = envelope.Id, IssueId = envelope.IssueId, Content = envelope.Content ?? "" },
                        ct)
                    .ConfigureAwait(false);
                return;

            case NoticeKafkaKinds.GetById:
                await ReplyRpcAsync(envelope.CorrelationId, ct, async () =>
                {
                    var n = await app.GetByIdAsync(envelope.NoticeId, ct).ConfigureAwait(false);
                    return new NoticeOutEnvelope { Ok = true, Notice = n, CorrelationId = envelope.CorrelationId! };
                }).ConfigureAwait(false);
                return;

            case NoticeKafkaKinds.GetPage:
                await ReplyRpcAsync(envelope.CorrelationId, ct, async () =>
                {
                    var page = await app.GetPageAsync(
                            envelope.Page, envelope.Size, envelope.Sort,
                            envelope.IssueId <= 0 ? null : envelope.IssueId,
                            ct)
                        .ConfigureAwait(false);
                    return new NoticeOutEnvelope { Ok = true, Page = page, CorrelationId = envelope.CorrelationId! };
                }).ConfigureAwait(false);
                return;

            case NoticeKafkaKinds.GetAll:
                await ReplyRpcAsync(envelope.CorrelationId, ct, async () =>
                {
                    var list = await app.GetAllAsync(envelope.Sort, ct).ConfigureAwait(false);
                    return new NoticeOutEnvelope
                    {
                        Ok = true,
                        Notices = list.ToList(),
                        CorrelationId = envelope.CorrelationId!
                    };
                }).ConfigureAwait(false);
                return;

            case NoticeKafkaKinds.Update:
                await ReplyRpcAsync(envelope.CorrelationId, ct, async () =>
                {
                    var n = await app.UpdateAsync(
                            new NoticeRequestTo
                            {
                                Id = envelope.Id,
                                IssueId = envelope.IssueId,
                                Content = envelope.Content ?? ""
                            },
                            ct)
                        .ConfigureAwait(false);
                    return new NoticeOutEnvelope { Ok = true, Notice = n, CorrelationId = envelope.CorrelationId! };
                }).ConfigureAwait(false);
                return;

            case NoticeKafkaKinds.Delete:
                await ReplyRpcAsync(envelope.CorrelationId, ct, async () =>
                {
                    await app.DeleteAsync(envelope.NoticeId, ct).ConfigureAwait(false);
                    return new NoticeOutEnvelope { Ok = true, CorrelationId = envelope.CorrelationId! };
                }).ConfigureAwait(false);
                return;

            default:
                _logger.LogWarning("Unknown notice kind {Kind}", envelope.Kind);
                return;
        }
    }

    private async Task ReplyRpcAsync(string? correlationId, CancellationToken ct, Func<Task<NoticeOutEnvelope>> buildOk)
    {
        if (string.IsNullOrEmpty(correlationId))
        {
            _logger.LogWarning("RPC envelope without correlationId");
            return;
        }

        NoticeOutEnvelope result;
        try
        {
            result = await buildOk().ConfigureAwait(false);
            result.Ok = true;
        }
        catch (KeyNotFoundException ex)
        {
            result = new NoticeOutEnvelope
                { CorrelationId = correlationId, Ok = false, Error = ex.Message };
        }
        catch (ArgumentException ex)
        {
            result = new NoticeOutEnvelope
                { CorrelationId = correlationId, Ok = false, Error = ex.Message };
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "RPC handler failed");
            result = new NoticeOutEnvelope { CorrelationId = correlationId, Ok = false, Error = ex.Message };
        }

        var json = DiscussionKafkaJson.SerializeNoticeOut(result);
        await _producer.ProduceAsync(
                _opt.OutTopic,
                new Message<string, string> { Key = correlationId, Value = json },
                ct)
            .ConfigureAwait(false);
    }
}
