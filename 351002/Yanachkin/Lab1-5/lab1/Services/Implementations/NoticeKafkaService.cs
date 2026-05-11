using System.Globalization;
using System.Text.Json;
using Confluent.Kafka;
using lab1.Infrastructure;
using lab1.Kafka;
using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Repositories.Interfaces;
using lab1.Services.Interfaces;
using Microsoft.Extensions.Options;

namespace lab1.Services.Implementations;

/// <summary>Транспорт Notice между publisher и discussion через Kafka InTopic / OutTopic.</summary>
public class NoticeKafkaService : INoticeService
{
    private readonly IProducer<string, string> _producer;
    private readonly KafkaTransportOptions _opt;
    private readonly NoticeRpcCorrelationRegistry _rpc;
    private readonly IIssueRepository _issueRepository;

    public NoticeKafkaService(
        IProducer<string, string> producer,
        IOptions<KafkaTransportOptions> kafkaOptions,
        NoticeRpcCorrelationRegistry rpc,
        IIssueRepository issueRepository)
    {
        _producer = producer;
        _opt = kafkaOptions.Value;
        _rpc = rpc;
        _issueRepository = issueRepository;
    }

    public async Task<NoticeResponseTo> CreateAsync(NoticeRequestTo request, CancellationToken cancellationToken = default)
    {
        Validate(request);
        await EnsureIssueExistsAsync(request.IssueId, cancellationToken).ConfigureAwait(false);

        var id = NoticeIdGenerator.NextId();
        var trimmed = request.Content.Trim();

        await ProduceFireAndForgetAsync(
                new NoticeInEnvelope
                {
                    Kind = NoticeKafkaKinds.CreateDraft,
                    Id = id,
                    IssueId = request.IssueId,
                    Content = trimmed,
                },
                cancellationToken)
            .ConfigureAwait(false);

        return new NoticeResponseTo
        {
            Id = id,
            IssueId = request.IssueId,
            Content = trimmed,
            State = "PENDING"
        };
    }

    public async Task<NoticeResponseTo> GetByIdAsync(long id, CancellationToken cancellationToken = default)
    {
        var env = new NoticeInEnvelope
        {
            Kind = NoticeKafkaKinds.GetById,
            NoticeId = id
        };

        var reply = await SendRpcAsync(env, cancellationToken).ConfigureAwait(false);
        ThrowIfFailed(reply);
        return reply.Notice ?? throw new KeyNotFoundException("Notice not found");
    }

    public async Task<PageResponseTo<NoticeResponseTo>> GetPageAsync(
        int page,
        int size,
        string? sort,
        long? issueId,
        CancellationToken cancellationToken = default)
    {
        var env = new NoticeInEnvelope
        {
            Kind = NoticeKafkaKinds.GetPage,
            Page = page,
            Size = size,
            Sort = sort,
            IssueId = issueId ?? 0
        };

        var reply = await SendRpcAsync(env, cancellationToken).ConfigureAwait(false);
        ThrowIfFailed(reply);
        return reply.Page ?? new PageResponseTo<NoticeResponseTo>();
    }

    public async Task<IReadOnlyList<NoticeResponseTo>> GetAllAsync(CancellationToken cancellationToken = default)
    {
        var env = new NoticeInEnvelope { Kind = NoticeKafkaKinds.GetAll };
        var reply = await SendRpcAsync(env, cancellationToken).ConfigureAwait(false);
        ThrowIfFailed(reply);
        return reply.Notices ?? [];
    }

    public async Task<NoticeResponseTo> UpdateAsync(NoticeRequestTo request, CancellationToken cancellationToken = default)
    {
        Validate(request);
        await EnsureIssueExistsAsync(request.IssueId, cancellationToken).ConfigureAwait(false);

        var env = new NoticeInEnvelope
        {
            Kind = NoticeKafkaKinds.Update,
            Id = request.Id,
            IssueId = request.IssueId,
            Content = request.Content.Trim()
        };

        var reply = await SendRpcAsync(env, cancellationToken).ConfigureAwait(false);
        ThrowIfFailed(reply);
        return reply.Notice ?? throw new KeyNotFoundException("Notice not found");
    }

    public async Task DeleteAsync(long id, CancellationToken cancellationToken = default)
    {
        var env = new NoticeInEnvelope { Kind = NoticeKafkaKinds.Delete, NoticeId = id };
        var reply = await SendRpcAsync(env, cancellationToken).ConfigureAwait(false);
        ThrowIfFailed(reply);
    }

    public async Task<PageResponseTo<NoticeResponseTo>> GetPageByIssueAsync(
        long issueId,
        int page,
        int size,
        string? sort,
        CancellationToken cancellationToken = default)
        => await GetPageAsync(page, size, sort, issueId, cancellationToken).ConfigureAwait(false);

    private async Task<NoticeOutEnvelope> SendRpcAsync(NoticeInEnvelope envelope, CancellationToken cancellationToken)
    {
        var correlationId = Guid.NewGuid().ToString("N");
        envelope.CorrelationId = correlationId;
        var timeout = TimeSpan.FromMilliseconds(_opt.RpcTimeoutMs);

        _rpc.Register(correlationId);
        try
        {
            await ProduceTransactionalAsync(envelope, cancellationToken).ConfigureAwait(false);
        }
        catch
        {
            _rpc.Abandon(correlationId);
            throw;
        }

        return await _rpc.WaitForAsync(correlationId, timeout, cancellationToken).ConfigureAwait(false);
    }

    private Task ProduceFireAndForgetAsync(NoticeInEnvelope envelope, CancellationToken cancellationToken)
        => ProduceToInTopicAsync(envelope, cancellationToken);

    private Task ProduceTransactionalAsync(NoticeInEnvelope envelope, CancellationToken cancellationToken)
        => ProduceToInTopicAsync(envelope, cancellationToken);

    private async Task ProduceToInTopicAsync(NoticeInEnvelope envelope, CancellationToken cancellationToken)
    {
        cancellationToken.ThrowIfCancellationRequested();
        var json = JsonSerializer.Serialize(envelope, PublisherKafkaJson.Options);
        var key = PartitionKey(envelope);
        await _producer
            .ProduceAsync(_opt.InTopic, new Message<string, string> { Key = key, Value = json }, cancellationToken)
            .ConfigureAwait(false);
    }

    private static string PartitionKey(NoticeInEnvelope envelope)
        => envelope.Kind switch
        {
            NoticeKafkaKinds.CreateDraft => envelope.IssueId.ToString(CultureInfo.InvariantCulture),
            NoticeKafkaKinds.Update => envelope.IssueId.ToString(CultureInfo.InvariantCulture),
            NoticeKafkaKinds.GetPage =>
                envelope.IssueId > 0
                    ? envelope.IssueId.ToString(CultureInfo.InvariantCulture)
                    : "scan",
            NoticeKafkaKinds.GetAll => "scan",
            _ => envelope.NoticeId > 0
                ? FormattableString.Invariant($"n-{envelope.NoticeId}")
                : "n-0"
        };

    private async Task EnsureIssueExistsAsync(long issueId, CancellationToken cancellationToken)
    {
        if (await _issueRepository.GetByIdAsync(issueId, cancellationToken).ConfigureAwait(false) == null)
            throw new ArgumentException("Issue not found");
    }

    private static void Validate(NoticeRequestTo request)
    {
        if (request.IssueId <= 0)
            throw new ArgumentException("IssueId must be positive");

        if (string.IsNullOrWhiteSpace(request.Content))
            throw new ArgumentException("Content must not be empty");

        if (request.Content.Trim().Length < 2)
            throw new ArgumentException("Content must be at least 2 characters");
    }

    private static void ThrowIfFailed(NoticeOutEnvelope env)
    {
        if (env.Ok)
            return;

        var err = env.Error ?? "";
        if (err.Contains("Notice not found", StringComparison.OrdinalIgnoreCase))
            throw new KeyNotFoundException("Notice not found");

        throw new ArgumentException(string.IsNullOrWhiteSpace(err) ? "Bad request" : err);
    }
}
