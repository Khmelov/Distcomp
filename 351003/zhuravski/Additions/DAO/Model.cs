using System.Linq.Expressions;
using System.Reflection;

namespace Additions.DAO;

public abstract class Model<T, X> where T : Model<T, X>
{
    public long Id {get; set;}
    public virtual T Clone()
    {
        return (T)MemberwiseClone();
    }

    private static readonly Dictionary<Type, Action<T, T>> copiers = [];
    private static readonly Lock copiersLock = new();
    
    public void CopyTo(T target)
    {
        var copier = Model<T, X>.GetCopier();
        copier((T)this, target);
    }
    
    public T CopyToNew()
    {
        var clone = Clone();
        CopyTo(clone);
        return clone;
    }
    
    private static Action<T, T> GetCopier()
    {
        var type = typeof(T);
        
        if (copiers.TryGetValue(type, out var copier))
            return copier;
        
        lock (copiersLock)
        {
            if (copiers.TryGetValue(type, out copier))
                return copier;
            
            copier = Model<T, X>.CompileCopier();
            copiers[type] = copier;
            return copier;
        }
    }
    
    private static Action<T, T> CompileCopier()
    {
        var sourceParam = Expression.Parameter(typeof(T), "source");
        var targetParam = Expression.Parameter(typeof(T), "target");
        
        var expressions = new List<Expression>();
        
        var properties = typeof(T).GetProperties(BindingFlags.Public | BindingFlags.Instance)
            .Where(p => p.CanRead && p.CanWrite);
        
        foreach (var prop in properties)
        {
            var sourceProp = Expression.Property(sourceParam, prop);
            var targetProp = Expression.Property(targetParam, prop);
            var assign = Expression.Assign(targetProp, sourceProp);
            expressions.Add(assign);
        }
        
        var block = Expression.Block(expressions);
        
        return Expression.Lambda<Action<T, T>>(block, sourceParam, targetParam).Compile();
    }
}