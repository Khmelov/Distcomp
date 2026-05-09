using System.Collections.Concurrent;
using System.Text.Json;
using Confluent.Kafka;
using Publisher.Application.Exceptions;
using Publisher.Application.Services.Interfaces;
using Publisher.Domain.Interfaces;
using Shared.Constants;
using Shared.DTOs.Requests;
using Shared.DTOs.Responses;
using Shared.Enums;
using Shared.Interfaces;
using Shared.Messaging;

namespace Publisher.Application.Services;

public class KafkaCommentService : ICommentService
{
    private static readonly ConcurrentDictionary<string, TaskCompletionSource<KafkaResponse>> _pendingRequests = new();
    private readonly ICacheService _cache;
    private readonly IIssueRepository _issueRepository;
    private readonly IProducer<string, string> _producer;

    public KafkaCommentService(IProducer<string, string> producer, IIssueRepository issueRepository,
        ICacheService cache)
    {
        _producer = producer;
        _issueRepository = issueRepository;
        _cache = cache;
    }


    public async Task<CommentResponseTo> CreateAsync(CommentRequestTo request)
    {
        var issueExists = await _issueRepository.GetByIdAsync(request.IssueId);
        if (issueExists == null) throw new RestException(400, 27, $"Issue with id {request.IssueId} does not exist.");

        var generatedId = DateTime.UtcNow.Ticks & long.MaxValue;


        var enrichedRequest = request with
        {
            Id = generatedId,
            State = CommentState.PENDING
        };

        var kafkaRequest = new KafkaRequest
        {
            Method = "CREATE",
            Payload = enrichedRequest,
            CorrelationId = Guid.NewGuid().ToString()
        };

        await _producer.ProduceAsync(KafkaTopics.InTopic, new Message<string, string>
        {
            Key = request.IssueId.ToString(),
            Value = JsonSerializer.Serialize(kafkaRequest)
        });


        var response = new CommentResponseTo
        {
            Id = generatedId, IssueId = request.IssueId, Content = request.Content, State = CommentState.PENDING
        };


        await _cache.SetAsync($"Comment:{generatedId}", response);

        await _cache.RemoveAsync($"CommentsByIssue:{request.IssueId}");

        return response;
    }


    public async Task<IEnumerable<CommentResponseTo>> GetAllAsync()
    {
        var response = await SendRequestAndWaitResponse("GET_ALL", new CommentRequestTo(), "broadcast");

        return response.PayloadList ?? new List<CommentResponseTo>();
    }

    public async Task<CommentResponseTo> GetByIdAsync(long id)
    {
        var key = $"Comment:{id}";

        var cached = await _cache.GetAsync<CommentResponseTo>(key);
        if (cached != null) return cached;

        var response = await SendRequestAndWaitResponse("GET_BY_ID", new CommentRequestTo { Id = id }, id.ToString());

        if (!response.IsSuccess)
            throw new RestException(404, 45, response.ErrorMessage ?? "Comment not found");

        await _cache.SetAsync(key, response.Payload!);

        return response.Payload!;
    }

    public async Task<IEnumerable<CommentResponseTo>> GetByIssueIdAsync(long issueId)
    {
        var key = $"CommentsByIssue:{issueId}";

        var cachedList = await _cache.GetAsync<List<CommentResponseTo>>(key);
        if (cachedList != null) return cachedList;

        var response = await SendRequestAndWaitResponse("GET_BY_ISSUE", new CommentRequestTo { IssueId = issueId },
            issueId.ToString());
        var results = response.PayloadList ?? new List<CommentResponseTo>();

        if (results.Any()) await _cache.SetAsync(key, results);

        return results;
    }

    public async Task<CommentResponseTo> UpdateAsync(CommentRequestTo request)
    {
        var response = await SendRequestAndWaitResponse("UPDATE", request, request.IssueId.ToString());
        if (!response.IsSuccess) throw new RestException(404, 45, "Comment not found");


        await _cache.SetAsync($"Comment:{request.Id}", response.Payload!);
        await _cache.RemoveAsync($"CommentsByIssue:{request.IssueId}");

        return response.Payload!;
    }

    public async Task<bool> DeleteAsync(long id)
    {
        var existing = await GetByIdAsync(id);

        var response = await SendRequestAndWaitResponse("DELETE", new CommentRequestTo { Id = id }, id.ToString());
        if (!response.IsSuccess) throw new RestException(404, 45, "Comment not found");

        await _cache.RemoveAsync($"Comment:{id}");
        await _cache.RemoveAsync($"CommentsByIssue:{existing.IssueId}");

        return true;
    }

    public async Task DeleteByIssueIdAsync(long issueId)
    {
        await _producer.ProduceAsync(KafkaTopics.InTopic, new Message<string, string>
        {
            Key = issueId.ToString(),
            Value = JsonSerializer.Serialize(new KafkaRequest
            {
                Method = "DELETE_BY_ISSUE", Payload = new CommentRequestTo { IssueId = issueId }, CorrelationId = ""
            })
        });

        await _cache.RemoveAsync($"CommentsByIssue:{issueId}");
    }


    public static void HandleResponse(KafkaResponse response)
    {
        if (_pendingRequests.TryRemove(response.CorrelationId, out var tcs)) tcs.SetResult(response);
    }


    private async Task<KafkaResponse> SendRequestAndWaitResponse(string method, CommentRequestTo payload, string key)
    {
        var correlationId = Guid.NewGuid().ToString();
        var tcs = new TaskCompletionSource<KafkaResponse>(TaskCreationOptions.RunContinuationsAsynchronously);
        _pendingRequests[correlationId] = tcs;

        var request = new KafkaRequest
        {
            Method = method,
            Payload = payload,
            CorrelationId = correlationId
        };


        await _producer.ProduceAsync(KafkaTopics.InTopic, new Message<string, string>
        {
            Key = key,
            Value = JsonSerializer.Serialize(request)
        });


        var completedTask = await Task.WhenAny(tcs.Task, Task.Delay(1000));

        if (completedTask == tcs.Task) return await tcs.Task;

        _pendingRequests.TryRemove(correlationId, out _);

        throw new RestException(504, 00, "Discussion service timeout via Kafka");
    }
}