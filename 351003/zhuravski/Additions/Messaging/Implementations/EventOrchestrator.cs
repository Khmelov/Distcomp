using System.Collections.Concurrent;
using Additions.Messaging.Interfaces;

namespace Additions.Messaging.Implementations;

public class EventOrchestrator : IEventOrchestrator
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
                throw new MessagingException(
                    $"No response received for MessageId {messageId} within {TIMEOUT}");
            }
            EventMessage result = await tcs.Task;
            if (result.Error != null)
            {
                throw new MessagingException(result.Error);
            }
            return result;
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