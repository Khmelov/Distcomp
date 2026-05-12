using AutoMapper;
using Microsoft.EntityFrameworkCore;
using RestApiTask.Data;
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
        private readonly IRepository<Marker> _markerRepo;
        private readonly ICacheService _cache;
        private readonly AppDbContext _db;
        private readonly IMapper _mapper;
        private const string CacheKeyPrefix = "article:";
        private const string CacheKeyAll = "articles:all";

        public ArticleService(
            IRepository<Article> repo,
            IRepository<Writer> writerRepo,
            IRepository<Marker> markerRepo,
            ICacheService cache,
            AppDbContext db,
            IMapper mapper)
        {
            _repo = repo;
            _writerRepo = writerRepo;
            _markerRepo = markerRepo;
            _cache = cache;
            _db = db;
            _mapper = mapper;
        }

        public async Task<IEnumerable<ArticleResponseTo>> GetAllAsync(QueryOptions? options = null)
        {
            if (options is null)
            {
                var cached = await _cache.GetAsync<IEnumerable<ArticleResponseTo>>(CacheKeyAll);
                if (cached != null)
                    return cached;

                var data = _mapper.Map<IEnumerable<ArticleResponseTo>>(await _repo.GetAllAsync());
                await _cache.SetAsync(CacheKeyAll, data);
                return data;
            }

            var page = await _repo.GetAllAsync(options);
            return _mapper.Map<IEnumerable<ArticleResponseTo>>(page.Items);
        }

        public async Task<ArticleResponseTo> GetByIdAsync(long id)
        {
            var cacheKey = CacheKeyPrefix + id;
            var cached = await _cache.GetAsync<ArticleResponseTo>(cacheKey);
            if (cached != null)
                return cached;

            var entity = await _repo.GetByIdAsync(id) ?? throw new NotFoundException("Article not found");
            var result = _mapper.Map<ArticleResponseTo>(entity);
            await _cache.SetAsync(cacheKey, result);
            return result;
        }

        public async Task<ArticleResponseTo> CreateAsync(ArticleRequestTo request)
        {
            await Validate(request);

            if (await ArticleExistsAsync(request.WriterId, request.Title))
                throw new ForbiddenException("Article with this title already exists for this writer");

            var entity = _mapper.Map<Article>(request);
            entity.Created = entity.Modified = DateTime.UtcNow;
            var created = await _repo.AddAsync(entity);
            await SyncMarkersAsync(created.Id, request.Markers);
            var result = _mapper.Map<ArticleResponseTo>(created);
            
            // Invalidate articles cache
            await _cache.RemoveAsync(CacheKeyAll);
            
            return result;
        }

        public async Task<ArticleResponseTo> UpdateAsync(long id, ArticleRequestTo request)
        {
            var existing = await _repo.GetByIdAsync(id) ?? throw new NotFoundException("Article not found");
            await Validate(request);

            if (await ArticleExistsAsync(request.WriterId, request.Title, excludeId: id))
                throw new ForbiddenException("Article with this title already exists for this writer");

            _mapper.Map(request, existing);
            existing.Modified = DateTime.UtcNow;
            await _repo.UpdateAsync(existing);
            await SyncMarkersAsync(existing.Id, request.Markers);
            var result = _mapper.Map<ArticleResponseTo>(existing);
            
            // Invalidate caches
            await _cache.RemoveAsync(CacheKeyPrefix + id);
            await _cache.RemoveAsync(CacheKeyAll);
            
            return result;
        }

        public async Task DeleteAsync(long id)
        {
            if (!await _repo.DeleteAsync(id)) throw new NotFoundException("Article not found");
            
            // Invalidate caches
            await _cache.RemoveAsync(CacheKeyPrefix + id);
            await _cache.RemoveAsync(CacheKeyAll);
        }

        private async Task Validate(ArticleRequestTo r)
        {
            if (r.Title.Length < 2 || r.Title.Length > 64) throw new ValidationException("Title: 2-64 chars");
            if (r.Content.Length < 4 || r.Content.Length > 2048) throw new ValidationException("Content: 4-2048 chars");
            if (await _writerRepo.GetByIdAsync(r.WriterId) == null) throw new ForbiddenException("Invalid WriterId");
            if (r.Markers is null) return;
            foreach (var marker in r.Markers.Where(m => !string.IsNullOrWhiteSpace(m)))
            {
                if (marker.Length is < 2 or > 32) throw new ValidationException("Marker name: 2-32 chars");
            }
        }

        private async Task SyncMarkersAsync(long articleId, List<string>? markers)
        {
            var markerNames = (markers ?? new List<string>())
                .Where(m => !string.IsNullOrWhiteSpace(m))
                .Select(m => m.Trim())
                .Distinct(StringComparer.OrdinalIgnoreCase)
                .ToList();

            await _db.Database.ExecuteSqlRawAsync("DELETE FROM tbl_article_marker WHERE article_id = {0}", articleId);

            if (markerNames.Count == 0) return;

            foreach (var markerName in markerNames)
            {
                var existing = await _markerRepo.GetAllAsync(new QueryOptions
                {
                    PageNumber = 1,
                    PageSize = 1,
                    Filter = $"name={markerName}"
                });

                long markerId;
                if (existing.TotalCount == 0)
                {
                    markerId = (await _markerRepo.AddAsync(new Marker { Name = markerName })).Id;
                }
                else
                {
                    markerId = existing.Items[0].Id;
                }

                await _db.Database.ExecuteSqlRawAsync(
                    "INSERT INTO tbl_article_marker(article_id, marker_id) VALUES ({0}, {1}) ON CONFLICT DO NOTHING",
                    articleId, markerId);
            }
        }
        private async Task<bool> ArticleExistsAsync(long writerId, string title, long excludeId = 0)
        {
            var articles = await _repo.GetAllAsync(new QueryOptions
            {
                PageNumber = 1,
                PageSize = 200,
                Filter = $"writerId={writerId},title={title}"
            });

            if (articles.TotalCount == 0) return false;
            if (excludeId == 0) return true;

            return articles.Items.Any(a => a.Id != excludeId);
        }
    }
}
