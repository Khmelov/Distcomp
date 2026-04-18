using Additions.Cache;
using Additions.DAO;
using Additions.Messaging;

namespace Additions.Service;

public abstract class BasicService
{
    protected static async Task InvokeLowerMethod(Func<Task> call)
    {
        try
        {
            await call();
        }
        catch (DAOException e)
        {
            HandleLowerException(e);
        }
        catch (MessagingException e)
        {
            HandleLowerException(e);
        }
        catch (CacheException e)
        {
            HandleLowerException(e);
        }
    }

    protected static Task InvokeDAOMethod(Func<Task> call)
    {
        return InvokeLowerMethod(call);
    }

    protected static async Task<T> InvokeLowerMethod<T>(Func<Task<T>> call)
    {
        try
        {
            return await call();
        }
        catch (DAOException e)
        {
            HandleLowerException(e);
            return default!;
        }
        catch (MessagingException e)
        {
            HandleLowerException(e);
            return default!;
        }
        catch (CacheException e)
        {
            HandleLowerException(e);
            return default!;
        }
    }

    protected static Task<T> InvokeDAOMethod<T>(Func<Task<T>> call)
    {
        return InvokeLowerMethod(call);
    }

    protected static void HandleLowerException(Exception e)
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