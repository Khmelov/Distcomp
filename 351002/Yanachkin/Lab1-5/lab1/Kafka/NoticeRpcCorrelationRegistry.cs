using System.Collections.Concurrent;

namespace lab1.Kafka;

public sealed class NoticeRpcCorrelationRegistry
{
    private readonly ConcurrentDictionary<string, TaskCompletionSource<NoticeOutEnvelope>> _pending = new();

    public void Register(string correlationId)
    {
        var tcs = new TaskCompletionSource<NoticeOutEnvelope>(TaskCreationOptions.RunContinuationsAsynchronously);
        if (!_pending.TryAdd(correlationId, tcs))
            throw new InvalidOperationException("Duplicate correlation id");
    }

    public async Task<NoticeOutEnvelope> WaitForAsync(string correlationId, TimeSpan timeout, CancellationToken cancellationToken)
    {
        if (!_pending.TryGetValue(correlationId, out var tcs))
            throw new InvalidOperationException("Correlation is not registered");

        try
        {
            using var ctr = CancellationTokenSource.CreateLinkedTokenSource(cancellationToken);
            ctr.CancelAfter(timeout);
            return await tcs.Task.WaitAsync(ctr.Token).ConfigureAwait(false);
        }
        catch (OperationCanceledException) when (!cancellationToken.IsCancellationRequested)
        {
            throw new TimeoutException("Kafka RPC reply timed out");
        }
        finally
        {
            _pending.TryRemove(correlationId, out _);
        }
    }

    public void Abandon(string correlationId)
    {
        if (_pending.TryRemove(correlationId, out var tcs))
            tcs.TrySetCanceled(CancellationToken.None);
    }

    public void TryComplete(NoticeOutEnvelope envelope)
    {
        if (string.IsNullOrEmpty(envelope.CorrelationId))
            return;
        if (_pending.TryGetValue(envelope.CorrelationId, out var tcs))
            tcs.TrySetResult(envelope);
    }
}
