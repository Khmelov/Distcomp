using RW.Discussion.DTOs;

namespace RW.Discussion.Services;

public interface INoteService
{
    Task<NoteResponseTo> CreateAsync(NoteRequestTo dto);
    Task<NoteResponseTo?> GetByIdAsync(long id);
    Task<IEnumerable<NoteResponseTo>> GetAllAsync();
    Task<NoteResponseTo?> UpdateAsync(NoteRequestTo dto);
    Task<bool> DeleteAsync(long id);
}
