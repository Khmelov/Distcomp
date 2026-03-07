
namespace Task310RestApi.Exceptions
{
    public class ValidationException : Exception
    {
        public string ErrorCode { get; }

        public ValidationException(string message, string errorCode) : base(message)
        {
            ErrorCode = errorCode;
        }
    }
}
