using System.Linq.Dynamic.Core;
using Microsoft.EntityFrameworkCore;
using ServerApp.Infrastructure;
using ServerApp.Models;
using ServerApp.Models.Entities;

namespace ServerApp.Repository;

public class EfRepository<T>(AppDbContext context) : IRepository<T> where T : BaseEntity
{
    public IEnumerable<T> GetPaged(QueryParams p)
    {
        var query = context.Set<T>().AsNoTracking();

        // Сортировка ("Id asc" или "Id desc")
        var sortExpression = $"{p.SortBy} {p.SortOrder}";
        query = query.OrderBy(sortExpression);

        // Пагинация
        return query
            .Skip((p.PageNumber - 1) * p.PageSize)
            .Take(p.PageSize)
            .ToList();
    }

    public IEnumerable<T> GetAll()
    {
        return context.Set<T>().AsNoTracking().ToList();
    }

    public T? GetById(long id)
    {
        var query = context.Set<T>().AsQueryable();

        if (typeof(T) == typeof(Article))
            query = query.Include("Stickers");
        else if (typeof(T) == typeof(Sticker))
            query = query.Include("Articles");
        else if (typeof(T) == typeof(Author)) query = query.Include("Articles").Include("Articles.Stickers");

        return query.FirstOrDefault(e => e.Id == id);
    }

    public T Create(T entity)
    {
        context.Set<T>().Add(entity);
        context.SaveChanges();
        return entity;
    }

    public T Update(T entity)
    {
        context.Set<T>().Update(entity);
        context.SaveChanges();
        return entity;
    }

    public bool Delete(long id)
    {
        var entity = GetById(id);
        if (entity == null) return false;

        context.Set<T>().Remove(entity);
        context.SaveChanges();
        return true;
    }
}