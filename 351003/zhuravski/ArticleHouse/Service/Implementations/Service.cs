using ArticleHouse.DAO.Exceptions;
using ArticleHouse.Service.Exceptions;

namespace ArticleHouse.Service.Implementations;

public abstract class Service
{
    protected static async Task InvokeDAOMethod(Func<Task> call)
    {
        try
        {
            await call();
        }
        catch (DAOException e)
        {
            HandleDAOException(e);
        }
    }

    protected static async Task<T> InvokeDAOMethod<T>(Func<Task<T>> call)
    {
        try
        {
            return await call();
        }
        catch (DAOException e)
        {
            HandleDAOException(e);
            return default!;
        }
    }

    protected static void HandleDAOException(DAOException e)
    {
        if (e is DAOObjectNotFoundException)
        {
            throw new ServiceObjectNotFoundException(e.Message);
        }
        throw new ServiceException(e.Message);
    }
}