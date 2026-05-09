using System.Collections.Concurrent;
using Infrastructure.Kafka;
using BusinessLogic.DTO.Response;

public class ModerationResultWaiter : IModerationResultWaiter
{
    private readonly ConcurrentDictionary<string, TaskCompletionSource<PostResponseTo?>> _singleRequests = new();
    private readonly ConcurrentDictionary<string, TaskCompletionSource<List<PostResponseTo>?>> _listRequests = new();

    public async Task<PostResponseTo?> WaitForResultAsync(int id, TimeSpan timeout)
    {
        var tcs = new TaskCompletionSource<PostResponseTo?>(TaskCreationOptions.RunContinuationsAsynchronously);
        var key = id.ToString();
        _singleRequests[key] = tcs;

        using var cts = new CancellationTokenSource(timeout);
        cts.Token.Register(() => tcs.TrySetResult(null));

        try { return await tcs.Task; }
        finally { _singleRequests.TryRemove(key, out _); }
    }

    public async Task<List<PostResponseTo>?> WaitForListResultAsync(string key, TimeSpan timeout)
    {
        var tcs = new TaskCompletionSource<List<PostResponseTo>?>(TaskCreationOptions.RunContinuationsAsynchronously);
        _listRequests[key] = tcs;

        using var cts = new CancellationTokenSource(timeout);
        cts.Token.Register(() => tcs.TrySetResult(null));

        try { return await tcs.Task; }
        finally { _listRequests.TryRemove(key, out _); }
    }

    public void SetResult(int id, PostResponseTo result)
    {
        if (_singleRequests.TryGetValue(id.ToString(), out var tcs))
            tcs.TrySetResult(result);
    }

    public void SetListResult(string key, List<PostResponseTo> result)
    {
        if (_listRequests.TryGetValue(key, out var tcs))
            tcs.TrySetResult(result);
    }
}