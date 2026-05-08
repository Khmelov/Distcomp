using AutoMapper;
using RestApiTask.Infrastructure.Exceptions;
using RestApiTask.Models.DTOs;
using RestApiTask.Models.Entities;
using RestApiTask.Repositories;
using RestApiTask.Services.Interfaces;

namespace RestApiTask.Services;

public class WriterService : IWriterService
{
    private readonly IRepository<Writer> _repo;
    private readonly ICacheService _cache;
    private readonly IMapper _mapper;
    private const string CacheKeyPrefix = "writer:";
    private const string CacheKeyAll = "writers:all";

    public WriterService(IRepository<Writer> repo, ICacheService cache, IMapper mapper)
    {
        _repo = repo;
        _cache = cache;
        _mapper = mapper;
    }

    public async Task<IEnumerable<WriterResponseTo>> GetAllAsync(QueryOptions? options = null)
    {
        if (options is null)
        {
            var cached = await _cache.GetAsync<IEnumerable<WriterResponseTo>>(CacheKeyAll);
            if (cached != null)
                return cached;

            var data = _mapper.Map<IEnumerable<WriterResponseTo>>(await _repo.GetAllAsync());
            await _cache.SetAsync(CacheKeyAll, data);
            return data;
        }

        var page = await _repo.GetAllAsync(options);
        return _mapper.Map<IEnumerable<WriterResponseTo>>(page.Items);
    }

    public async Task<WriterResponseTo> GetByIdAsync(long id)
    {
        var cacheKey = CacheKeyPrefix + id;
        var cached = await _cache.GetAsync<WriterResponseTo>(cacheKey);
        if (cached != null)
            return cached;

        var entity = await _repo.GetByIdAsync(id) ?? throw new NotFoundException("Writer not found");
        var result = _mapper.Map<WriterResponseTo>(entity);
        await _cache.SetAsync(cacheKey, result);
        return result;
    }

    public async Task<WriterResponseTo> CreateAsync(WriterRequestTo request)
    {
        Validate(request);
        var entity = _mapper.Map<Writer>(request);
        var created = await _repo.AddAsync(entity);
        var result = _mapper.Map<WriterResponseTo>(created);
        
        // Invalidate all writers cache
        await _cache.RemoveAsync(CacheKeyAll);
        
        return result;
    }

    public async Task<WriterResponseTo> UpdateAsync(long id, WriterRequestTo request)
    {
        var existing = await _repo.GetByIdAsync(id) ?? throw new NotFoundException("Writer not found");
        Validate(request);
        _mapper.Map(request, existing);
        await _repo.UpdateAsync(existing);
        var result = _mapper.Map<WriterResponseTo>(existing);
        
        // Invalidate caches
        await _cache.RemoveAsync(CacheKeyPrefix + id);
        await _cache.RemoveAsync(CacheKeyAll);
        
        return result;
    }

    public async Task DeleteAsync(long id)
    {
        if (!await _repo.DeleteAsync(id)) throw new NotFoundException("Writer not found");
        
        // Invalidate caches
        await _cache.RemoveAsync(CacheKeyPrefix + id);
        await _cache.RemoveAsync(CacheKeyAll);
    }

    private void Validate(WriterRequestTo r)
    {
        if (r.Login.Length < 2 || r.Login.Length > 64) throw new ValidationException("Login: 2-64 chars");
        if (r.Password.Length < 8 || r.Password.Length > 128) throw new ValidationException("Password: 8-128 chars");
        if (r.Firstname.Length < 2 || r.Firstname.Length > 64) throw new ValidationException("Firstname: 2-64 chars");
        if (r.Lastname.Length < 2 || r.Lastname.Length > 64) throw new ValidationException("Lastname: 2-64 chars");
    }
}