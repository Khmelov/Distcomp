using AutoMapper;
using RestApiTask.Infrastructure.Exceptions;
using RestApiTask.Models.DTOs;
using RestApiTask.Models.Entities;
using RestApiTask.Repositories;
using RestApiTask.Services.Interfaces;

namespace RestApiTask.Services
{
    public class ArticleService : IArticleService
    {
        private readonly IRepository<Article> _repo;
        private readonly IRepository<Writer> _writerRepo;
        private readonly IMapper _mapper;

        public ArticleService(IRepository<Article> repo, IRepository<Writer> writerRepo, IMapper mapper)
        {
            _repo = repo;
            _writerRepo = writerRepo;
            _mapper = mapper;
        }

        public async Task<IEnumerable<ArticleResponseTo>> GetAllAsync() =>
            _mapper.Map<IEnumerable<ArticleResponseTo>>(await _repo.GetAllAsync());

        public async Task<ArticleResponseTo> GetByIdAsync(long id)
        {
            var entity = await _repo.GetByIdAsync(id) ?? throw new NotFoundException("Article not found");
            return _mapper.Map<ArticleResponseTo>(entity);
        }

        public async Task<ArticleResponseTo> CreateAsync(ArticleRequestTo request)
        {
            await Validate(request);
            var entity = _mapper.Map<Article>(request);
            entity.Created = entity.Modified = DateTime.UtcNow;
            return _mapper.Map<ArticleResponseTo>(await _repo.AddAsync(entity));
        }

        public async Task<ArticleResponseTo> UpdateAsync(long id, ArticleRequestTo request)
        {
            var existing = await _repo.GetByIdAsync(id) ?? throw new NotFoundException("Article not found");
            await Validate(request);
            _mapper.Map(request, existing);
            existing.Modified = DateTime.UtcNow;
            await _repo.UpdateAsync(existing);
            return _mapper.Map<ArticleResponseTo>(existing);
        }

        public async Task DeleteAsync(long id)
        {
            if (!await _repo.DeleteAsync(id)) throw new NotFoundException("Article not found");
        }

        private async Task Validate(ArticleRequestTo r)
        {
            if (r.Title.Length < 2 || r.Title.Length > 64) throw new ValidationException("Title: 2-64 chars");
            if (r.Content.Length < 4 || r.Content.Length > 2048) throw new ValidationException("Content: 4-2048 chars");
            if (await _writerRepo.GetByIdAsync(r.WriterId) == null) throw new ValidationException("Invalid WriterId");
        }
    }
}
