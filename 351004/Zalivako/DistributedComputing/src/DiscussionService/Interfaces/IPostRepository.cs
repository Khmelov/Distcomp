using DiscussionService.Models;

namespace DiscussionService.Interfaces
{
    public interface IPostRepository
    {
        Task<Post> AddAsync(Post post);
        Task<Post?> GetByIdAsync(long id);
        Task<List<Post>> GetAllAsync();
        Task<Post?> UpdateAsync(Post post);
        Task DeleteAsync(Post post);
    }
}