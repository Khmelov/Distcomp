using ArticleHouse.Service.Exceptions;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse.Service.Implementations;

public abstract class Service
{
    protected static async Task InvokeDAOMethod(Func<Task> call)
    {
        try
        {
            await call();
        }
        catch (DbUpdateException e)
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
        catch (DbUpdateException e)
        {
            HandleDAOException(e);
            return default!;
        }
    }

    protected static void HandleDAOException(Exception e)
    {
        if (e is DbUpdateException)
        {
            throw new ServiceForbiddenOperationException(e.Message);
        }
        throw new ServiceException(e.Message);
    }
}