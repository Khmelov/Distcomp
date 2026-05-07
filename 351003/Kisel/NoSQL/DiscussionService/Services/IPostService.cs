using DiscussionService.Models.Dtos;
using DiscussionService.Models.Entities;

namespace DiscussionService.Services;

public interface IPostService
{
    Task<IEnumerable<Post>> GetAllAsync();
    Task<Post?> GetByIdAsync(int id); // Изменено на int
    Task<Post> CreateAsync(CreatePostDto dto);
    Task UpdateAsync(int id, PostDto dto); // Изменено на int
    Task DeleteAsync(int id); // Изменено на int
}