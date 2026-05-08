using DiscussionModule.models;

namespace DiscussionModule.interfaces;

public interface INoteRepository
{
    Task<Note> AddAsync(Note note);
    Task<Note?> GetByIdAsync(long id);
    Task<List<Note>> GetAllAsync();
    Task<Note?> UpdateAsync(Note note);
    Task DeleteAsync(Note note);

}