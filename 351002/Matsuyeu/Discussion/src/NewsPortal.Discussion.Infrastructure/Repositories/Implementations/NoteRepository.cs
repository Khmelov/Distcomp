// src/Discussion/NewsPortal.Discussion.Infrastructure/Repositories/NoteRepository.cs
using Cassandra.Mapping;
using Discussion.src.NewsPortal.Discussion.Domain.Entities;
using Discussion.src.NewsPortal.Discussion.Infrastructure.Data;
using Discussion.src.NewsPortal.Discussion.Infrastructure.Repositories.Abstractions;

namespace Discussion.src.NewsPortal.Discussion.Infrastructure.Repositories.Implementations;

public class NoteRepository : INoteRepository
{
    private readonly CassandraDbContext _context;
    private readonly ILogger<NoteRepository> _logger;
    private readonly IMapper _mapper;

    public NoteRepository(CassandraDbContext context, ILogger<NoteRepository> logger)
    {
        _context = context;
        _logger = logger;
        _mapper = context.Mapper;
    }

    public async Task<IEnumerable<Note>> GetAllAsync()
    {
        try
        {
            // Внимание: Это может быть медленно для больших таблиц!
            var query = "SELECT * FROM tbl_note";
            return await _mapper.FetchAsync<Note>(query);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting all notes");
            throw;
        }
    }

    public async Task<Note?> GetByIdAsync(long id)
    {
        try
        {
            var query = "SELECT * FROM tbl_note WHERE id = ? ALLOW FILTERING";
            var notes = await _mapper.FetchAsync<Note>(query, id);
            return notes.FirstOrDefault();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting note by id {Id}", id);
            throw;
        }
    }

    public async Task<Note> AddAsync(Note entity)
    {
        try
        {
            // Генерируем новый id для данной новости
            entity.Id = await GetNextIdAsync(entity.NewsId);

            // Вставляем
            await _mapper.InsertAsync(entity);

            _logger.LogInformation("Added note with id {Id} for news {NewsId}", entity.Id, entity.NewsId);
            return entity;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error adding note for news {NewsId}", entity.NewsId);
            throw;
        }
    }

    public async Task UpdateAsync(Note entity)
    {
        try
        {
            // Обновляем только content (created не меняем)
            var query = "UPDATE tbl_note SET content = ? WHERE news_id = ? AND id = ?";
            await _mapper.ExecuteAsync(query, entity.Content, entity.NewsId, entity.Id);

            _logger.LogInformation("Updated note with id {Id} for news {NewsId}", entity.Id, entity.NewsId);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error updating note {Id}", entity.Id);
            throw;
        }
    }

    public async Task DeleteAsync(long id)
    {
        try
        {
            // Сначала нужно найти note по id, чтобы узнать news_id
            var note = await GetByIdAsync(id);
            if (note == null)
            {
                _logger.LogWarning("Note {Id} not found for deletion", id);
                return;
            }

            // Удаляем используя составной ключ
            var query = "DELETE FROM tbl_note WHERE news_id = ? AND id = ?";
            await _mapper.ExecuteAsync(query, note.NewsId, id);

            _logger.LogInformation("Deleted note with id {Id}", id);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error deleting note {Id}", id);
            throw;
        }
    }

    public async Task<bool> ExistsAsync(long id)
    {
        try
        {
            var note = await GetByIdAsync(id);
            return note != null;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error checking existence of note {Id}", id);
            return false;
        }
    }

    // Вспомогательный метод для генерации следующего ID
    private async Task<long> GetNextIdAsync(long newsId)
    {
        try
        {
            var query = "SELECT MAX(id) as max_id FROM tbl_note WHERE news_id = ?";
            var result = await _mapper.FirstOrDefaultAsync<dynamic>(query, newsId);

            long maxId = result?.max_id ?? 0;
            return maxId + 1;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting next id for news {NewsId}", newsId);
            // Fallback: используем timestamp
            return DateTime.UtcNow.Ticks;
        }
    }
}