using AutoMapper;
using Cassandra.Mapping;
using Discussion.Entities;
using Discussion.Repositories;
using Shared.Dtos;
using IMapper = AutoMapper.IMapper;

namespace Discussion.Services
{
    public interface IReactionService
    {
        Task<ReactionResponseTo?> GetByIdAsync(long topicId, long id);
        Task<IEnumerable<ReactionResponseTo>> GetByTopicIdAsync(long topicId);
        Task<ReactionResponseTo> CreateAsync(ReactionRequestTo request);
        Task<ReactionResponseTo> UpdateAsync(ReactionRequestTo request);
        Task DeleteAsync(long topicId, long id);
        Task<ReactionResponseTo?> GetByIdOnlyAsync(long id);
    }

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
            // Генерация нового Id (можно через TimeUuid, но для простоты – временная метка)
            entity.Id = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
            await _repository.AddAsync(entity);
            return _mapper.Map<ReactionResponseTo>(entity);
        }

        public async Task<ReactionResponseTo> UpdateAsync(ReactionRequestTo request)
        {
            var existing = await _repository.GetByIdAsync(request.TopicId, request.Id);
            if (existing == null) throw new KeyNotFoundException();
            existing.Content = request.Content;
            await _repository.UpdateAsync(existing);
            return _mapper.Map<ReactionResponseTo>(existing);
        }

        public async Task DeleteAsync(long topicId, long id)
        {
            await _repository.DeleteAsync(topicId, id);
        }
    }
}