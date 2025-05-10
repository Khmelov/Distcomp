using Discussion.Models;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Discussion.Repositories
{
    public interface IReactionRepository
    {
        Task<IEnumerable<Reaction>> GetAllAsync();
        Task<Reaction?> GetByIdAsync(long id);
        Task CreateAsync(Reaction reaction);
        Task UpdateAsync(long id, Reaction reaction);
        Task DeleteAsync(long id);
    }
}
