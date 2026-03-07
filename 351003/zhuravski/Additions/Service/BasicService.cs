using Additions.DAO;

namespace Additions.Service;

public abstract class BasicService
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

    protected static void HandleDAOException(Exception e)
    {
        if (e is DAOUpdateException)
        {
            throw new ServiceForbiddenOperationException(e.Message);
        }
        if (e is DAOObjectNotFoundException)
        {
            throw new ServiceObjectNotFoundException(e.Message);
        }
        throw new ServiceException(e.Message);
    }
}