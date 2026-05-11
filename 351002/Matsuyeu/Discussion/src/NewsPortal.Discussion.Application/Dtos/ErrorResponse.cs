namespace Discussion.src.NewsPortal.Discussion.Application.Dtos;

public class ErrorResponse
{
    public string ErrorMessage { get; set; } = string.Empty;
    public string ErrorCode { get; set; } = string.Empty;

    public ErrorResponse(string errorMessage, string errorCode)
    {
        ErrorMessage = errorMessage;
        ErrorCode = errorCode;
    }
}