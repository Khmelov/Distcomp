namespace lab1.Models.DTO.Responses
{
    public class ErrorResponse
    {
        public int ErrorCode { get; set; }
        public string ErrorMessage { get; set; } = string.Empty;
    }
}
