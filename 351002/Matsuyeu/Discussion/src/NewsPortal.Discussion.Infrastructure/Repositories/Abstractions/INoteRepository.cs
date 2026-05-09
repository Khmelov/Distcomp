using Discussion.src.NewsPortal.Discussion.Domain.Entities;

namespace Discussion.src.NewsPortal.Discussion.Infrastructure.Repositories.Abstractions
{ 
    public interface INoteRepository
    {
        // Базовые CRUD операции
        Task<IEnumerable<Note>> GetAllAsync();
        Task<Note?> GetByIdAsync(long id);
        Task<Note> AddAsync(Note entity);
        Task UpdateAsync(Note entity);
        Task DeleteAsync(long id);
        Task<bool> ExistsAsync(long id);
    }
}