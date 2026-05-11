using AutoMapper;
using RestApiTask.Infrastructure.Exceptions;
using RestApiTask.Models.DTOs;
using RestApiTask.Models.Entities;
using RestApiTask.Repositories;
using RestApiTask.Services.Interfaces;

namespace RestApiTask.Services
{
    public class MarkerService : IMarkerService
    {
        private readonly IRepository<Marker> _repo;
        private readonly ICacheService _cache;
        private readonly IMapper _mapper;
        private const string CacheKeyPrefix = "marker:";
        private const string CacheKeyAll = "markers:all";

        public MarkerService(IRepository<Marker> repo, ICacheService cache, IMapper mapper)
        {
            _repo = repo;
            _cache = cache;
            _mapper = mapper;
        }

        public async Task<IEnumerable<MarkerResponseTo>> GetAllAsync(QueryOptions? options = null)
        {
            if (options is null)
            {
                var cached = await _cache.GetAsync<IEnumerable<MarkerResponseTo>>(CacheKeyAll);
                if (cached != null)
                    return cached;

                var page = await _repo.GetAllAsync(new QueryOptions
                {
                    PageNumber = 1,
                    PageSize = 200,
                    SortBy = "id",
                    SortOrder = "asc"
                });
                var data = _mapper.Map<IEnumerable<MarkerResponseTo>>(page.Items);
                await _cache.SetAsync(CacheKeyAll, data);
                return data;
            }

            var pageResult = await _repo.GetAllAsync(options);
            return _mapper.Map<IEnumerable<MarkerResponseTo>>(pageResult.Items);
        }

        public async Task<MarkerResponseTo> GetByIdAsync(long id)
        {
            if (id <= 0)
            {
                throw new ValidationException("Invalid marker ID");
            }

            var cacheKey = CacheKeyPrefix + id;
            var cached = await _cache.GetAsync<MarkerResponseTo>(cacheKey);
            if (cached != null)
                return cached;

            var entity = await _repo.GetByIdAsync(id)
                ?? throw new NotFoundException("Marker not found");

            var result = _mapper.Map<MarkerResponseTo>(entity);
            await _cache.SetAsync(cacheKey, result);
            return result;
        }

        public async Task<MarkerResponseTo> CreateAsync(MarkerRequestTo request)
        {
            if (request is null)
            {
                throw new ValidationException("Request cannot be null");
            }

            if (string.IsNullOrWhiteSpace(request.Name))
            {
                throw new ValidationException("Name cannot be empty");
            }

            if (request.Name.Length < 2 || request.Name.Length > 32)
            {
                throw new ValidationException("Name: 2-32 chars");
            }

            var entity = _mapper.Map<Marker>(request);
            var createdEntity = await _repo.AddAsync(entity);

            await EnsureDataConsistencyAsync();
            
            // Invalidate markers cache
            await _cache.RemoveAsync(CacheKeyAll);

            return _mapper.Map<MarkerResponseTo>(createdEntity);
        }

        public async Task<MarkerResponseTo> UpdateAsync(long id, MarkerRequestTo request)
        {
            if (id <= 0)
            {
                throw new ValidationException("Invalid marker ID");
            }

            if (request is null)
            {
                throw new ValidationException("Request cannot be null");
            }

            if (string.IsNullOrWhiteSpace(request.Name))
            {
                throw new ValidationException("Name cannot be empty");
            }

            if (request.Name.Length < 2 || request.Name.Length > 32)
            {
                throw new ValidationException("Name: 2-32 chars");
            }

            var existing = await _repo.GetByIdAsync(id)
                ?? throw new NotFoundException("Marker not found");

            _mapper.Map(request, existing);
            await _repo.UpdateAsync(existing);

            await EnsureDataConsistencyAsync();
            
            // Invalidate caches
            await _cache.RemoveAsync(CacheKeyPrefix + id);
            await _cache.RemoveAsync(CacheKeyAll);

            return _mapper.Map<MarkerResponseTo>(existing);
        }

        public async Task DeleteAsync(long id)
        {
            if (id <= 0)
            {
                throw new ValidationException("Invalid marker ID");
            }

            var deleted = await _repo.DeleteAsync(id);

            if (!deleted)
            {
                throw new NotFoundException("Marker not found");
            }

            await EnsureDataConsistencyAsync();
            
            // Invalidate caches
            await _cache.RemoveAsync(CacheKeyPrefix + id);
            await _cache.RemoveAsync(CacheKeyAll);
        }
        private async Task EnsureDataConsistencyAsync()
        {
            await _repo.GetAllAsync(new QueryOptions
            {
                PageNumber = 1,
                PageSize = 1,
                SortBy = "id",
                SortOrder = "asc"
            });
        }
    }
}