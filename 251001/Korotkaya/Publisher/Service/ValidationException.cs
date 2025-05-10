namespace WebApplication1.Service
{
    public class ValidationException : Exception
    {
        public int HttpCode { get; }
        public string ErrorCode { get; }

        public ValidationException(string message, int httpCode, string errorCode)
            : base(message)
        {
            HttpCode = httpCode;
            ErrorCode = errorCode;
        }
    }
}
