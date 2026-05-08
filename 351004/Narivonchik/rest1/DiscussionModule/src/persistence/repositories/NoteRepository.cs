using Cassandra;
using Cassandra.Mapping;
using DiscussionModule.interfaces;
using DiscussionModule.models;

namespace DiscussionModule.persistence.repositories;

public class NoteRepository : INoteRepository
{
    private readonly IMapper _mapper;
    private readonly Cassandra.ISession _session;

    public NoteRepository(CassandraContext context)
    {
        _mapper = context.Mapper;
        _session = context.Session;
    }

    public async Task<Note> AddAsync(Note note)
    {
        note.Id = await GetNextId();
        
        await _mapper.InsertAsync(note);
        return note;
    }

    public async Task<Note?> GetByIdAsync(long id)
    {
        var note = await _mapper.SingleOrDefaultAsync<Note>(
            "WHERE id = ?", id);
        return note;
    }

    public async Task<List<Note>> GetAllAsync()
    {
        var notes = await _mapper.FetchAsync<Note>();
        return notes.ToList();
    }

    public async Task<Note?> UpdateAsync(Note note)
    {
        var existing = await GetByIdAsync(note.Id);
        if (existing == null)
            return null;

        await _mapper.UpdateAsync(note);
        return note;
    }

    public async Task DeleteAsync(Note note)
    {
        await _mapper.DeleteAsync(note);
    }

    private async Task<long> GetNextId()
    {
        try
        {
            var updateQuery = @"
                UPDATE tbl_counters 
                SET counter_value = counter_value + 1 
                WHERE counter_name = 'note_id'";
            
            await _session.ExecuteAsync(new SimpleStatement(updateQuery));
            
            var selectQuery = @"
                SELECT counter_value FROM tbl_counters 
                WHERE counter_name = 'note_id'";
            
            var row = (await _session.ExecuteAsync(
                new SimpleStatement(selectQuery))).FirstOrDefault();
            
            if (row != null)
            {
                return row.GetValue<long>("counter_value");
            }
            
            var initQuery = @"
                UPDATE tbl_counters 
                SET counter_value = counter_value + 0 
                WHERE counter_name = 'note_id'";
            
            await _session.ExecuteAsync(new SimpleStatement(initQuery));
            
            var initRow = (await _session.ExecuteAsync(
                new SimpleStatement(selectQuery))).FirstOrDefault();
            
            return initRow?.GetValue<long>("counter_value") ?? 0;
        }
        catch (Exception ex)
        {
            throw new Exception("Ошибка при генерации ID для заметки", ex);
        }
    }
}