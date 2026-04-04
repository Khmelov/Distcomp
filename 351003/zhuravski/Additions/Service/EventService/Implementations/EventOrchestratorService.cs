using System.Collections.Concurrent;
using Additions.Service.EventService.Interfaces;

namespace Additions.Service.EventService.Implementations;

public class EventOrchestratorService : IEventOrchestratorService
{
    private static readonly TimeSpan TIMEOUT = TimeSpan.FromSeconds(5);
    private readonly ConcurrentDictionary<string, TaskCompletionSource<EventMessage>> pendingRequests = [];

    public async Task<EventMessage> ExpectResponse(Guid origin)
    {
        TaskCompletionSource<EventMessage> tcs = new();
        string messageId = origin.ToString();
        pendingRequests[messageId] = tcs;
        try
        {
            Task timeoutTask = Task.Delay(TIMEOUT);
            var completed = await Task.WhenAny(tcs.Task, timeoutTask);
            if (completed == timeoutTask)
            {
                throw new ServiceException(
                    $"No response received for MessageId {messageId} within {TIMEOUT}");
            }
            return await tcs.Task;
        }
        finally
        {
            pendingRequests.TryRemove(messageId, out _);
        }
    }

    public void ResolveResponse(EventMessage message)
    {
        if (message.InReplyTo != null) {
            string repliedId = message.InReplyTo.ToString()!;
            if (pendingRequests.TryGetValue(repliedId, out var tcs))
            {
                tcs.TrySetResult(message);
            }
        }
    }
}