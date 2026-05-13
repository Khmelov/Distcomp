namespace discussion.Models.DTO.Responses;

public class ErrorResponse
{
    public int ErrorCode { get; set; }
    public string ErrorMessage { get; set; } = null!;
}
