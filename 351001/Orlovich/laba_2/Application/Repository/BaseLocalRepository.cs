using Application.Repository;
using Domain.Models;

namespace Infrastructe.Repository;

public abstract class BaseLocalRepository<T> : IRepository<T>
    where T:class,IIdEntity
{
    protected readonly List<T> _storage = new();
    
    public virtual async Task<IList<T>> GetAllAsync()
    {
        await Task.CompletedTask;
        return _storage.ToList();
    }

    public virtual async Task<T?> GetByIdAsync(long id)
    {
        await Task.CompletedTask;
        var src =_storage.Find(a => a.id == id);
        
        if(src == null)
            return null;

        return Copy(src);
    }

    public virtual async Task AddAsync(T editor)
    {
        await Task.CompletedTask;
        _storage.Add(editor);
    }

    public abstract Task<T?> UpdateAsync(long id, T editor);

    public virtual async Task<int> DeleteAsync(long id)
    {
        await Task.CompletedTask;
        
        var count = _storage.RemoveAll(a => a.id == id);
        return count;
    }
    
    public abstract T Copy(T src);
}