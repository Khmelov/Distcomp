using System.Text.Json.Serialization;

namespace Publisher.Application.DTOs.Responses;

public record ErrorResponse
{
    [JsonPropertyName("errorMessage")]
    public string ErrorMessage { get; init; } = null!;

    [JsonPropertyName("errorCode")]
    public int ErrorCode { get; init; }

    public ErrorResponse() { }
    public ErrorResponse(string errorMessage, int errorCode)
    {
        ErrorMessage = errorMessage;
        ErrorCode = errorCode;
    }
}