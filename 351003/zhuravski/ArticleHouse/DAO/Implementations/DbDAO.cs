using System.Linq.Expressions;
using System.Reflection;
using Additions.DAO;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse.DAO.Implementations;

public class DbDAO<T> : IBasicDAO<T> where T : Model<T>
{
    protected readonly ApplicationContext db;
    protected readonly DbSet<T> dbSet;

    public DbDAO(ApplicationContext db, Expression<Func<ApplicationContext, DbSet<T>>> propertyFinder) {
        this.db = db;
        dbSet = FindDbSet(propertyFinder);
    }

    private DbSet<T> FindDbSet(Expression<Func<ApplicationContext, DbSet<T>>> propertyFinder)
    {
        if (propertyFinder.Body is MemberExpression expression)
        {
            MemberInfo member = expression.Member;
            if (member.MemberType == MemberTypes.Property)
            {
                PropertyInfo property = (PropertyInfo)member;
                object? value = property.GetValue(db);
                if (null != value)
                {
                    return (DbSet<T>)value;
                }
            }
        }
        throw new ArgumentException($"Couldn't create DbDAO from {typeof(T)}.");
    }

    public async Task<T[]> GetAllAsync()
    {
        return await dbSet.ToArrayAsync();
    }

    public async Task<T> AddNewAsync(T model)
    {
        T result = model.Clone();
        await dbSet.AddAsync(result);
        try
        {
            await db.SaveChangesAsync();
        }
        catch (DbUpdateException)
        {
            throw new DAOUpdateException("Object creation failure.");
        }
        return result;
    }

    public async Task DeleteAsync(long id)
    {
        T? model = await dbSet.FirstOrDefaultAsync(o => o.Id == id);
        if (null == model)
        {
            throw new DAOObjectNotFoundException();
        }
        dbSet.Remove(model);
        try
        {
            await db.SaveChangesAsync();
        }
        catch (DbUpdateException)
        {
            throw new DAOException("Deletion failure.");
        }
    }

    public async Task<T> GetByIdAsync(long id)
    {
        T? model = await dbSet.FirstOrDefaultAsync(o => o.Id == id);
        if (null == model)
        {
            throw new DAOObjectNotFoundException();
        }
        return model;
    }

    public async Task<T> UpdateAsync(T input)
    {
        T? model = await dbSet.FirstOrDefaultAsync(o => o.Id == input.Id);
        if (null == model) {
            throw new DAOObjectNotFoundException();
        }
        input.CopyTo(model);
        try
        {
            await db.SaveChangesAsync();
        }
        catch (DbUpdateException)
        {
            throw new DAOUpdateException("Update failure.");
        }
        return model;
    }
}