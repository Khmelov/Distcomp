using System.Collections.Concurrent;
using SharedModels;

namespace ServerApp.Services;

public class KafkaRequestManager
{
    private readonly ConcurrentDictionary<string, TaskCompletionSource<KafkaEvent>> _requests = new();

    public void Add(string id, TaskCompletionSource<KafkaEvent> tcs)
    {
        _requests.TryAdd(id, tcs);
    }

    public void Resolve(KafkaEvent ev)
    {
        if (_requests.TryRemove(ev.CorrelationId, out var tcs)) tcs.TrySetResult(ev);
    }
}