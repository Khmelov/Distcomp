using DiscussionService.Models.Dtos;
using DiscussionService.Models.Entities;

namespace DiscussionService.Repositories;

public interface IPostRepository
{
    Task<IEnumerable<Post>> GetAllAsync();
    Task<Post?> GetByIdAsync(int id); // Изменено на int
    Task<Post> CreateAsync(Post post);
    Task UpdateAsync(int id, PostDto dto); // Изменено на int
    Task DeleteAsync(int id); // Изменено на int
}