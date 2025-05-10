using Discussion.Models;

namespace Discussion.Repositories;

public interface INoticeRepository
{
    Task<List<Notice>> GetAllAsync();
    Task<Notice> GetByIdAsync(int id);
    Task CreateAsync(Notice notice);
    Task UpdateAsync(int id, Notice notice);
    Task DeleteAsync(int id);
}