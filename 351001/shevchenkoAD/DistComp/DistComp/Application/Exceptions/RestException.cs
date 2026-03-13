namespace DistComp.Application.Exceptions;

public class RestException : Exception {
    public RestException(int statusCode,
                         int subCode,
                         string message)
        : base(message) {
        StatusCode = statusCode;
        SubCode = subCode;
    }

    public int StatusCode { get; }
    public int SubCode { get; }
}