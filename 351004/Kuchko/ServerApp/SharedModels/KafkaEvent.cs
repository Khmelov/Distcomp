namespace SharedModels;

public class KafkaEvent
{
    public string CorrelationId { get; set; } = Guid.NewGuid().ToString(); // Уникальный ID для Request-Reply
    public string Action { get; set; } = string.Empty; // CREATE, GET, UPDATE, DELETE
    public long ArticleId { get; set; } // Ключ для партиционирования
    public string Payload { get; set; } = string.Empty; // Сами данные (JSON)
    public string ErrorMessage { get; set; } = string.Empty; 
}

public static class MessageState
{
    public const string Pending = "PENDING";
    public const string Approve = "APPROVE";
    public const string Decline = "DECLINE";
}