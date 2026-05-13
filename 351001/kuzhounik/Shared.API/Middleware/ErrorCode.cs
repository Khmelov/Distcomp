namespace Shared.Controllers.Middleware;

public class ErrorCode
{
    public int StatusCode { get; set; }
    public string Message { get; set; }
    public string? Details { get; set; }
}