using Discussion.Models;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Discussion.Services
{
    public interface IReactionService
    {
        Task<IEnumerable<Reaction>> GetAllReactionsAsync();
        Task<Reaction?> GetReactionByIdAsync(long id);
        Task<Reaction> CreateReactionAsync(Reaction reaction);
        Task<Reaction> UpdateReactionAsync(long id, Reaction reaction);
        Task DeleteReactionAsync(long id);
    }
}
