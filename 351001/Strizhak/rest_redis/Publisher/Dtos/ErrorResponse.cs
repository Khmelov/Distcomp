namespace Publisher.Dtos
{
    public class ErrorResponse
    {
        public string ErrorMessage { get; set; } = string.Empty;

        
        public int ErrorCode { get; set; }

        public ErrorResponse() { }

        public ErrorResponse(string message, int httpCode, int subCode = 1)
        {
            ErrorMessage = message;
            ErrorCode = httpCode * 100 + subCode;
        }
    }
}