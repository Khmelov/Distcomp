namespace Distcomp.Application.Exceptions
{
    public class RestException : Exception
    {
        public int HttpStatusCode { get; }
        public int ErrorCode { get; } 

        public RestException(int httpStatusCode, int errorCode, string message) : base(message)
        {
            HttpStatusCode = httpStatusCode;
            ErrorCode = errorCode;
        }
    }
}
