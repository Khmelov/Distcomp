using Discussion.Models;
using Discussion.Repositories;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Discussion.Services
{
    public class ReactionService : IReactionService
    {
        private readonly IReactionRepository _reactionRepository;
        public ReactionService(IReactionRepository reactionRepository)
        {
            _reactionRepository = reactionRepository;
        }

        public async Task<IEnumerable<Reaction>> GetAllReactionsAsync()
        {
            return await _reactionRepository.GetAllAsync();
        }

        public async Task<Reaction?> GetReactionByIdAsync(long id)
        {
            return await _reactionRepository.GetByIdAsync(id);
        }

        public async Task<Reaction> CreateReactionAsync(Reaction reaction)
        {
            if (reaction.Id == 0)
            {
                reaction.Id = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
            }
            await _reactionRepository.CreateAsync(reaction);
            return reaction;
        }

        public async Task<Reaction> UpdateReactionAsync(long id, Reaction reaction)
        {
            var existing = await GetReactionByIdAsync(id);
            if (existing == null)
                throw new KeyNotFoundException();
            reaction.Modified = DateTime.UtcNow;
            await _reactionRepository.UpdateAsync(id, reaction);
            return reaction;
        }

        public async Task DeleteReactionAsync(long id)
        {
            await _reactionRepository.DeleteAsync(id);
        }
    }
}
