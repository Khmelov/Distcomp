using Microsoft.EntityFrameworkCore;
using RestApiTask.Data;
using RestApiTask.Models.Entities;
using System.Globalization;
using System.Linq.Expressions;
using System.Reflection;

namespace RestApiTask.Repositories;

public sealed class EfRepository<T> : IRepository<T> where T : class, IHasId
{
    private readonly AppDbContext _db;

    public EfRepository(AppDbContext db) => _db = db;

    public async Task<IEnumerable<T>> GetAllAsync() =>
        await _db.Set<T>().AsNoTracking().ToListAsync();

    public async Task<PagedResult<T>> GetAllAsync(QueryOptions options)
    {
        var pageNumber = options.PageNumber < 1 ? 1 : options.PageNumber;
        var pageSize = options.PageSize is < 1 ? 1 : options.PageSize;
        if (pageSize > 200) pageSize = 200;

        IQueryable<T> query = _db.Set<T>().AsNoTracking();

        if (!string.IsNullOrWhiteSpace(options.Filter))
        {
            query = ApplyFilter(query, options.Filter!);
        }

        if (!string.IsNullOrWhiteSpace(options.SortBy))
        {
            query = ApplySort(query, options.SortBy!, options.SortOrder);
        }

        var total = await query.LongCountAsync();
        var items = await query
            .Skip((pageNumber - 1) * pageSize)
            .Take(pageSize)
            .ToListAsync();

        return new PagedResult<T>(items, total, pageNumber, pageSize);
    }

    public async Task<T?> GetByIdAsync(long id) =>
        await _db.Set<T>().FindAsync(id);

    public async Task<T> AddAsync(T entity)
    {
        _db.Set<T>().Add(entity);
        await _db.SaveChangesAsync();
        return entity;
    }

    public async Task<T> UpdateAsync(T entity)
    {
        _db.Set<T>().Update(entity);
        await _db.SaveChangesAsync();
        return entity;
    }

    public async Task<bool> DeleteAsync(long id)
    {
        var existing = await _db.Set<T>().FindAsync(id);
        if (existing is null) return false;
        _db.Set<T>().Remove(existing);
        await _db.SaveChangesAsync();
        return true;
    }

    private static IQueryable<T> ApplySort(IQueryable<T> source, string sortBy, string sortOrder)
    {
        var prop = typeof(T).GetProperty(sortBy, BindingFlags.Instance | BindingFlags.Public | BindingFlags.IgnoreCase);
        // Deterministic ordering to avoid EF warnings about Skip/Take without OrderBy.
        if (prop is null) return source.OrderBy(e => e.Id);

        var param = Expression.Parameter(typeof(T), "e");
        var body = Expression.Convert(Expression.Property(param, prop), typeof(object));
        var keySelector = Expression.Lambda<Func<T, object>>(body, param);

        var desc = string.Equals(sortOrder, "desc", StringComparison.OrdinalIgnoreCase);
        return desc ? source.OrderByDescending(keySelector) : source.OrderBy(keySelector);
    }

    private static IQueryable<T> ApplyFilter(IQueryable<T> source, string filter)
    {
        // "field=value,other~=substring"
        var clauses = filter.Split(',', StringSplitOptions.RemoveEmptyEntries | StringSplitOptions.TrimEntries);
        foreach (var clause in clauses)
        {
            var op = clause.Contains("~=", StringComparison.Ordinal) ? "~=" : "=";
            var parts = clause.Split(op, 2, StringSplitOptions.TrimEntries);
            if (parts.Length != 2) continue;

            var field = parts[0];
            var rawValue = parts[1];
            if (string.IsNullOrWhiteSpace(field)) continue;

            var prop = typeof(T).GetProperty(field, BindingFlags.Instance | BindingFlags.Public | BindingFlags.IgnoreCase);
            if (prop is null) continue;

            source = source.Where(BuildPredicate(prop, op, rawValue));
        }
        return source;
    }

    private static Expression<Func<T, bool>> BuildPredicate(PropertyInfo prop, string op, string rawValue)
    {
        var param = Expression.Parameter(typeof(T), "e");
        var member = Expression.Property(param, prop);

        if (prop.PropertyType == typeof(string))
        {
            var toLower = typeof(string).GetMethod(nameof(string.ToLower), Type.EmptyTypes)!;
            var contains = typeof(string).GetMethod(nameof(string.Contains), new[] { typeof(string) })!;

            var left = Expression.Call(member, toLower);
            var rightConst = Expression.Constant(rawValue.ToLowerInvariant());

            Expression body = op == "~="
                ? Expression.Call(left, contains, rightConst)
                : Expression.Equal(left, rightConst);

            return Expression.Lambda<Func<T, bool>>(body, param);
        }

        var targetType = Nullable.GetUnderlyingType(prop.PropertyType) ?? prop.PropertyType;
        object? converted = TryConvert(rawValue, targetType);
        if (converted is null)
        {
            return _ => false;
        }

        var constant = Expression.Constant(converted, targetType);
        Expression leftTyped = prop.PropertyType == targetType ? member : Expression.Convert(member, targetType);
        var equals = Expression.Equal(leftTyped, constant);
        return Expression.Lambda<Func<T, bool>>(equals, param);
    }

    private static object? TryConvert(string rawValue, Type targetType)
    {
        try
        {
            if (targetType == typeof(Guid)) return Guid.Parse(rawValue);
            if (targetType.IsEnum) return Enum.Parse(targetType, rawValue, ignoreCase: true);
            return Convert.ChangeType(rawValue, targetType, CultureInfo.InvariantCulture);
        }
        catch
        {
            return null;
        }
    }
}

