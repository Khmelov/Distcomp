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
        private readonly IRepository<Article> _articleRepo;
        private readonly IMapper _mapper;

        public MessageService(IRepository<Message> repo, IRepository<Article> articleRepo, IMapper mapper)
        {
            _repo = repo;
            _articleRepo = articleRepo;
            _mapper = mapper;
        }

        public async Task<IEnumerable<MessageResponseTo>> GetAllAsync() =>
            _mapper.Map<IEnumerable<MessageResponseTo>>(await _repo.GetAllAsync());

        public async Task<MessageResponseTo> GetByIdAsync(long id)
        {
            var entity = await _repo.GetByIdAsync(id) ?? throw new NotFoundException("Message not found");
            return _mapper.Map<MessageResponseTo>(entity);
        }

        public async Task<MessageResponseTo> CreateAsync(MessageRequestTo request)
        {
            await Validate(request);
            var entity = _mapper.Map<Message>(request);
            return _mapper.Map<MessageResponseTo>(await _repo.AddAsync(entity));
        }

        public async Task<MessageResponseTo> UpdateAsync(long id, MessageRequestTo request)
        {
            var existing = await _repo.GetByIdAsync(id) ?? throw new NotFoundException("Message not found");
            await Validate(request);
            _mapper.Map(request, existing);
            await _repo.UpdateAsync(existing);
            return _mapper.Map<MessageResponseTo>(existing);
        }

        public async Task DeleteAsync(long id)
        {
            if (!await _repo.DeleteAsync(id)) throw new NotFoundException("Message not found");
        }

        private async Task Validate(MessageRequestTo r)
        {
            if (r.Content.Length < 2 || r.Content.Length > 2048) throw new ValidationException("Content: 2-2048 chars");
            if (await _articleRepo.GetByIdAsync(r.ArticleId) == null) throw new ValidationException("Invalid ArticleId");
        }
    }
}
