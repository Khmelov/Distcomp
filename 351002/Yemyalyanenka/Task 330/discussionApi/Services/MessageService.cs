using AutoMapper;
using RestApiTask.Infrastructure.Exceptions;
using RestApiTask.Models.DTOs;
using RestApiTask.Models.Entities;
using RestApiTask.Repositories;
using RestApiTask.Services.Interfaces;

namespace RestApiTask.Services
{
    public class MessageService : IMessageService
    {
        private readonly IRepository<Message> _repo;
        private readonly IMapper _mapper;

        public MessageService(IRepository<Message> repo, IMapper mapper)
        {
            _repo = repo;
            _mapper = mapper;
        }

        public async Task<IEnumerable<MessageResponseTo>> GetAllAsync(QueryOptions? options = null)
        {
            if (options is null)
            {
                return _mapper.Map<IEnumerable<MessageResponseTo>>(await _repo.GetAllAsync());
            }

            var page = await _repo.GetAllAsync(options);
            return _mapper.Map<IEnumerable<MessageResponseTo>>(page.Items);
        }

        public async Task<MessageResponseTo> GetByIdAsync(long id)
        {
            var entity = await _repo.GetByIdAsync(id) ?? throw new NotFoundException("Message not found");
            return _mapper.Map<MessageResponseTo>(entity);
        }

        public async Task<MessageResponseTo> CreateAsync(MessageRequestTo request)
        {
            Validate(request);
            var entity = _mapper.Map<Message>(request);
            return _mapper.Map<MessageResponseTo>(await _repo.AddAsync(entity));
        }

        public async Task<MessageResponseTo> UpdateAsync(long id, MessageRequestTo request)
        {
            var existing = await _repo.GetByIdAsync(id) ?? throw new NotFoundException("Message not found");
            Validate(request);
            _mapper.Map(request, existing);
            await _repo.UpdateAsync(existing);
            return _mapper.Map<MessageResponseTo>(existing);
        }

        public async Task DeleteAsync(long id)
        {
            if (!await _repo.DeleteAsync(id)) throw new NotFoundException("Message not found");
        }

        private static void Validate(MessageRequestTo r)
        {
            if (r.Content.Length < 2 || r.Content.Length > 2048)
                throw new ValidationException("Content: 2-2048 chars");
            if (r.ArticleId <= 0)
                throw new ForbiddenException("Invalid ArticleId");
        }
    }
}