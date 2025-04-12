namespace External.API.Response;

public class ErrorModel
{
    public string ErrorMessage { get; set; } = null!;
    public int ErrorCode { get; set; }
}