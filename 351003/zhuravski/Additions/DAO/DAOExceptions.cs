namespace Additions.DAO;

public class DAOException : Exception
{
    public DAOException() {}
    public DAOException(string? message) : base(message) { }
}

public class DAOObjectNotFoundException : DAOException
{
    public DAOObjectNotFoundException() {}
    public DAOObjectNotFoundException(string? message) : base(message) { }
}

public class DAOUpdateException : DAOException
{
    public DAOUpdateException() {}
    public DAOUpdateException(string? message) : base(message) { }
}