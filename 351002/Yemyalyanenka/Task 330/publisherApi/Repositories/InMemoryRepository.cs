using RestApiTask.Models.Entities;
using System.Collections.Concurrent;
using System.Globalization;
using System.Reflection;

namespace RestApiTask.Repositories;

public class InMemoryRepository<T> : IRepository<T> where T : class, IHasId
{
    private readonly ConcurrentDictionary<long, T> _storage = new();
    private long _currentId = 0;

    public Task<IEnumerable<T>> GetAllAsync()
    {
        return Task.FromResult(_storage.Values.AsEnumerable());
    }

    public Task<PagedResult<T>> GetAllAsync(QueryOptions options)
    {
        var pageNumber = options.PageNumber < 1 ? 1 : options.PageNumber;
        var pageSize = options.PageSize is < 1 ? 1 : options.PageSize;
        if (pageSize > 200) pageSize = 200;

        IEnumerable<T> query = _storage.Values;

        if (!string.IsNullOrWhiteSpace(options.Filter))
        {
            query = ApplyFilter(query, options.Filter!);
        }

        if (!string.IsNullOrWhiteSpace(options.SortBy))
        {
            query = ApplySort(query, options.SortBy!, options.SortOrder);
        }

        var total = query.LongCount();
        var items = query
            .Skip((pageNumber - 1) * pageSize)
            .Take(pageSize)
            .ToList();

        return Task.FromResult(new PagedResult<T>(items, total, pageNumber, pageSize));
    }

    public Task<T?> GetByIdAsync(long id)
    {
        _storage.TryGetValue(id, out var entity);
        return Task.FromResult(entity);
    }

    public Task<T> AddAsync(T entity)
    {
        // Атомарно увеличиваем ID
        long id = Interlocked.Increment(ref _currentId);
        entity.Id = id;
        _storage[id] = entity;
        return Task.FromResult(entity);
    }

    public Task<T> UpdateAsync(T entity)
    {
        if (_storage.ContainsKey(entity.Id))
        {
            _storage[entity.Id] = entity;
            return Task.FromResult(entity);
        }
        throw new KeyNotFoundException($"Entity with id {entity.Id} not found.");
    }

    public Task<bool> DeleteAsync(long id)
    {
        return Task.FromResult(_storage.TryRemove(id, out _));
    }

    private static IEnumerable<T> ApplyFilter(IEnumerable<T> source, string filter)
    {
        // format: "field=value,other~=substring"
        // "=" => equals (case-insensitive for strings)
        // "~=" => contains (case-insensitive)
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

            source = source.Where(e => MatchesClause(e, prop, op, rawValue));
        }
        return source;
    }

    private static bool MatchesClause(T entity, PropertyInfo prop, string op, string rawValue)
    {
        var value = prop.GetValue(entity);
        if (value is null) return false;

        if (value is string s)
        {
            if (op == "~=") return s.Contains(rawValue, StringComparison.OrdinalIgnoreCase);
            return string.Equals(s, rawValue, StringComparison.OrdinalIgnoreCase);
        }

        var targetType = Nullable.GetUnderlyingType(prop.PropertyType) ?? prop.PropertyType;
        try
        {
            object converted =
                targetType == typeof(Guid) ? Guid.Parse(rawValue) :
                targetType.IsEnum ? Enum.Parse(targetType, rawValue, ignoreCase: true) :
                Convert.ChangeType(rawValue, targetType, CultureInfo.InvariantCulture);

            return value.Equals(converted);
        }
        catch
        {
            return false;
        }
    }

    private static IEnumerable<T> ApplySort(IEnumerable<T> source, string sortBy, string sortOrder)
    {
        var prop = typeof(T).GetProperty(sortBy, BindingFlags.Instance | BindingFlags.Public | BindingFlags.IgnoreCase);
        // Deterministic ordering to make paging stable when SortBy is invalid/missing.
        if (prop is null) return source.OrderBy(e => e.Id);

        var desc = string.Equals(sortOrder, "desc", StringComparison.OrdinalIgnoreCase);
        return desc
            ? source.OrderByDescending(e => prop.GetValue(e, null))
            : source.OrderBy(e => prop.GetValue(e, null));
    }
}