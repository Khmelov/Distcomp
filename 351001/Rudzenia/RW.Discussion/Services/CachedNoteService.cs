using RW.Discussion.Caching;
using RW.Discussion.DTOs;

namespace RW.Discussion.Services;

public class CachedNoteService : INoteService
{
    private readonly INoteService _inner;
    private readonly ICacheService _cache;
    private readonly ILogger<CachedNoteService> _logger;

    public CachedNoteService(
        INoteService inner,
        ICacheService cache,
        ILogger<CachedNoteService> logger)
    {
        _inner = inner;
        _cache = cache;
        _logger = logger;
    }

    public async Task<NoteResponseTo> CreateAsync(NoteRequestTo dto)
    {
        var created = await _inner.CreateAsync(dto);
        await _cache.SetAsync(CacheKeys.Note(created.Id), created);
        await _cache.RemoveAsync(CacheKeys.NotesAll);
        return created;
    }

    public async Task<NoteResponseTo?> GetByIdAsync(long id)
    {
        var key = CacheKeys.Note(id);
        var cached = await _cache.GetAsync<NoteResponseTo>(key);
        if (cached is not null)
        {
            _logger.LogInformation("Cache HIT for note {Id}", id);
            return cached;
        }

        _logger.LogInformation("Cache MISS for note {Id}", id);
        var note = await _inner.GetByIdAsync(id);
        if (note is not null)
            await _cache.SetAsync(key, note);

        return note;
    }

    public async Task<IEnumerable<NoteResponseTo>> GetAllAsync()
    {
        var cached = await _cache.GetAsync<List<NoteResponseTo>>(CacheKeys.NotesAll);
        if (cached is not null)
        {
            _logger.LogInformation("Cache HIT for all notes ({Count})", cached.Count);
            return cached;
        }

        _logger.LogInformation("Cache MISS for all notes");
        var notes = (await _inner.GetAllAsync()).ToList();
        await _cache.SetAsync(CacheKeys.NotesAll, notes);
        return notes;
    }

    public async Task<NoteResponseTo?> UpdateAsync(NoteRequestTo dto)
    {
        var updated = await _inner.UpdateAsync(dto);
        if (updated is not null)
        {
            await _cache.SetAsync(CacheKeys.Note(updated.Id), updated);
            await _cache.RemoveAsync(CacheKeys.NotesAll);
        }
        return updated;
    }

    public async Task<bool> DeleteAsync(long id)
    {
        var deleted = await _inner.DeleteAsync(id);
        if (deleted)
        {
            await _cache.RemoveAsync(CacheKeys.Note(id));
            await _cache.RemoveAsync(CacheKeys.NotesAll);
        }
        return deleted;
    }
}
