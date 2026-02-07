namespace ArticleHouse.DAO.Exceptions;

public class DAOException : Exception
{
    public DAOException() {}
    public DAOException(string? message) : base(message) { }
}