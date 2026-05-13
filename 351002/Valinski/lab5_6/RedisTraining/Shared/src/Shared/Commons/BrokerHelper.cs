using System.Collections.Concurrent;
using System.Text.Json;

namespace Shared.Commons;

public class BrokerHelper
{
    public ConcurrentDictionary<string, TaskCompletionSource<JsonElement?>> Dict;

    public BrokerHelper()
    {
        Dict = new ConcurrentDictionary<string, TaskCompletionSource<JsonElement?>>();
    }

    public TaskCompletionSource<JsonElement?> SetTask(string correlationId)
    {
        var tcs = new TaskCompletionSource<JsonElement?>(TaskCreationOptions.RunContinuationsAsynchronously);
        Dict.TryAdd(correlationId, tcs);
        return tcs;
    }

    public void TryRemove(string correlationId, JsonElement? value)
    {
        if (Dict.TryRemove(correlationId, out var tcs))
        {
            tcs.TrySetResult(value);
        }
    }
}
