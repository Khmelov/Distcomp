using AutoMapper;
using Cassandra.Mapping;
using Discussion.Entities;
using Discussion.Repositories;
using Shared.Dtos;
using IMapper = AutoMapper.IMapper;

namespace Discussion.Services
{
   

    public class ReactionService : IReactionService
    {
        private readonly IReactionRepository _repository;
        private readonly IMapper _mapper;

        public ReactionService(IReactionRepository repository, IMapper mapper)
        {
            _repository = repository;
            _mapper = mapper;
        }

        public async Task<ReactionResponseTo?> GetByIdAsync(long topicId, long id)
        {
            var entity = await _repository.GetByIdAsync(topicId, id);
            return entity == null ? null : _mapper.Map<ReactionResponseTo>(entity);
        }
        public async Task<ReactionResponseTo?> GetByIdOnlyAsync(long id)
        {
            var entity = await _repository.GetByIdOnlyAsync(id);
            return entity == null ? null : _mapper.Map<ReactionResponseTo>(entity);
        }

        public async Task<IEnumerable<ReactionResponseTo>> GetByTopicIdAsync(long topicId)
        {
            var entities = await _repository.GetByTopicIdAsync(topicId);
            return _mapper.Map<IEnumerable<ReactionResponseTo>>(entities);
        }

        public async Task<ReactionResponseTo> CreateAsync(ReactionRequestTo request)
        {
            var entity = _mapper.Map<Reaction>(request);
            await _repository.AddAsync(entity);
            return _mapper.Map<ReactionResponseTo>(entity);
        }

        public async Task<ReactionResponseTo> UpdateAsync(ReactionRequestTo request)
        {
            // В Kafka-обработке важно использовать оба ключа (TopicId и Id)
            var existing = await _repository.GetByIdAsync(request.TopicId, request.Id);

            if (existing == null)
            {
                existing = _mapper.Map<Reaction>(request);
            }
            else
            {
                existing.Content = request.Content;
                existing.State = request.State;
            }

            await _repository.UpdateAsync(existing);
            return _mapper.Map<ReactionResponseTo>(existing);
        }
        public async Task<ReactionResponseTo> UpdateStateAsync(long id, string state)
        {
            var entity = await _repository.GetByIdOnlyAsync(id);
            if (entity == null) throw new KeyNotFoundException();
            entity.State = state;
            await _repository.UpdateAsync(entity);
            return _mapper.Map<ReactionResponseTo>(entity);
        }
        public async Task<IEnumerable<ReactionResponseTo>> GetAllAsync()
        {
            var entities = await _repository.GetAllAsync();
            return _mapper.Map<IEnumerable<ReactionResponseTo>>(entities);
        }
        public async Task DeleteAsync(long id)
        {
            await _repository.DeleteAsync(id);
        }
    }
}