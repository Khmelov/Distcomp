using System.Collections.Concurrent;

namespace Publisher.Services
{
    public class KafkaResponseTracker
    {
        private readonly ConcurrentDictionary<string, TaskCompletionSource<string>> _pending = new();

        public Task<string> WaitForResponse(string correlationId)
        {
            var tcs = new TaskCompletionSource<string>(TaskCreationOptions.RunContinuationsAsynchronously);
            _pending[correlationId] = tcs;
            return tcs.Task;
        }

        public void CompleteResponse(string correlationId, string jsonResponse)
        {
            if (_pending.TryRemove(correlationId, out var tcs))
            {
                tcs.TrySetResult(jsonResponse);
            }
        }

        public void CancelWait(string correlationId)
        {
            if (_pending.TryRemove(correlationId, out var tcs))
            {
                tcs.TrySetCanceled();
            }
        }
    }
}