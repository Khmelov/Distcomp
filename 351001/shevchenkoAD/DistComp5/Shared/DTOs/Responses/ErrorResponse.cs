using System.Text.Json.Serialization;

namespace Shared.DTOs.Responses;

public record ErrorResponse
{
    public ErrorResponse()
    {
    }

    public ErrorResponse(string errorMessage, int errorCode)
    {
        ErrorMessage = errorMessage;
        ErrorCode = errorCode;
    }

    [JsonPropertyName("errorMessage")] public string ErrorMessage { get; init; } = null!;

    [JsonPropertyName("errorCode")] public int ErrorCode { get; init; }
}