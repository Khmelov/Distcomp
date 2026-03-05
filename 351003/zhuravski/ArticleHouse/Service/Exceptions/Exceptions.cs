namespace ArticleHouse.Service.Exceptions;

public class ServiceException : Exception
{
    public ServiceException() {}
    public ServiceException(string? message) : base(message) { }
}

public class ServiceObjectNotFoundException : ServiceException
{
    public ServiceObjectNotFoundException() {}
    public ServiceObjectNotFoundException(string? message) : base(message) { }
}

public class ServiceForbiddenOperationException : ServiceException
{
    public ServiceForbiddenOperationException() {}
    public ServiceForbiddenOperationException(string? message) : base(message) { }
}