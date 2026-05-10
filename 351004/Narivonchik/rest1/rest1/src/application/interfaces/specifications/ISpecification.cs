using System.Linq.Expressions;

namespace rest1.application.interfaces;

public interface ISpecification<T>
{
    //criteria expression
    Expression<Func<T, bool>> Criteria { get; }
        
    //join expressions
    List<Expression<Func<T, object>>> Includes { get; }
        
    //order by expressions
    Expression<Func<T, object>>? OrderBy { get; }
    Expression<Func<T, object>>? OrderByDescending { get; }
        
    //taken records count
    int? Take { get; }
    
    //skipped records count
    int? Skip { get; }
    
    //pagination flag
    bool IsPagingEnabled { get; }
}