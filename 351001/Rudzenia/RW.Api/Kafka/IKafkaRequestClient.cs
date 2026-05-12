namespace RW.Api.Kafka;

public interface IKafkaRequestClient
{
    Task<KafkaResponseEnvelope> SendAndWaitAsync(
        string method,
        object? payload,
        string key,
        CancellationToken cancellationToken = default);

    void HandleResponse(string correlationId, string responseValue);
}
