using System.Text.Json.Serialization;

namespace Application.DTOs.Responses;

public record ErrorResponse {
    public ErrorResponse(string errorMessage,
                         int errorCode) {
        ErrorMessage = errorMessage;
        ErrorCode = errorCode;
    }

    public string ErrorMessage { get; init; }

    [JsonPropertyName("errorCode")] public int ErrorCode { get; init; }
}