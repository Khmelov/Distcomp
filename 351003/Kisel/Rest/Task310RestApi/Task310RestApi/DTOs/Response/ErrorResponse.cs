using System.Text.Json.Serialization;

namespace Task310RestApi.DTOs.Response
{
    public class ErrorResponse
    {
        [JsonPropertyName("errorMessage")]
        public string ErrorMessage { get; set; } = string.Empty;

        [JsonPropertyName("errorCode")]
        public string ErrorCode { get; set; } = string.Empty;

        public ErrorResponse(string errorMessage, string errorCode)
        {
            ErrorMessage = errorMessage;
            ErrorCode = errorCode;
        }
    }
}