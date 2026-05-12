namespace RW.Application.Exceptions;

public class NotFoundException : Exception
{
    public NotFoundException(string entityName, long id)
        : base($"{entityName} with id {id} was not found.") { }
}
