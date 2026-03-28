namespace rest1.application.exceptions.db;

public class ForeignKeyViolationException : PersistenceException
{
    public ForeignKeyViolationException(string message) : base(message) { }

    public ForeignKeyViolationException(string message, Exception inner) : base(message, inner) { }
}