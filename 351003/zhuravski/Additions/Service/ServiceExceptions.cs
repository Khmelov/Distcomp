using Microsoft.AspNetCore.Http;

namespace Additions.Service;

public class ServiceException : Exception
{
    public int Code {get; init;}
    public ServiceException(int code = StatusCodes.Status400BadRequest)
    {
        Code = code;
    }
    public ServiceException(string? message,
                            int code = StatusCodes.Status400BadRequest) : base(message)
    {
        Code = code;
    }
}

public class ServiceObjectNotFoundException : ServiceException
{
    public ServiceObjectNotFoundException() : base(StatusCodes.Status404NotFound) {}
    public ServiceObjectNotFoundException(string? message) : base(message, StatusCodes.Status404NotFound) { }
}

public class ServiceForbiddenOperationException : ServiceException
{
    public ServiceForbiddenOperationException() : base(StatusCodes.Status403Forbidden) {}
    public ServiceForbiddenOperationException(string? message) : base(message, StatusCodes.Status403Forbidden) { }
}

public class ServiceFailedOperationException : ServiceException
{
    public ServiceFailedOperationException() {}
    public ServiceFailedOperationException(string? message) : base(message) { }
}