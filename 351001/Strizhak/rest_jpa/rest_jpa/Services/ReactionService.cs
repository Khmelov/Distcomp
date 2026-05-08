using AutoMapper;
using rest_api.Dtos;
using rest_api.Entities;
using rest_api.Repositories;
using System;
using System.Threading.Tasks;

namespace rest_api.Services
{
    public class ReactionService : BaseService<Reaction, ReactionRequestTo, ReactionResponseTo>
    {
        public ReactionService(IRepository<Reaction> repository, IMapper mapper)
            : base(repository, mapper)
        {
        }

        public override async Task<ReactionResponseTo> CreateAsync(ReactionRequestTo request)
        {
            var reaction = _mapper.Map<Reaction>(request);
           

            await _repository.AddAsync(reaction);
            await _repository.SaveChangesAsync();

            return _mapper.Map<ReactionResponseTo>(reaction);
        }

        public override async Task<ReactionResponseTo> UpdateAsync(long id, ReactionRequestTo request)
        {
            var existingReaction = await _repository.GetByIdAsync(id);
            if (existingReaction == null)
                throw new KeyNotFoundException($"Reaction with id {id} not found");

            _mapper.Map(request, existingReaction);

            _repository.Update(existingReaction);
            await _repository.SaveChangesAsync();

            return _mapper.Map<ReactionResponseTo>(existingReaction);
        }
    }
}