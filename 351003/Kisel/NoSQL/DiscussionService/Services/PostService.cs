using DiscussionService.Models.Dtos;
using DiscussionService.Models.Entities;
using DiscussionService.Repositories;

namespace DiscussionService.Services;

public class PostService : IPostService
{
    private readonly IPostRepository _repository;

    public PostService(IPostRepository repository)
    {
        _repository = repository;
    }

    public async Task<IEnumerable<Post>> GetAllAsync()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<Post?> GetByIdAsync(int id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<Post> CreateAsync(CreatePostDto dto)
    {
        var post = new Post
        {
            Id = Math.Abs(Guid.NewGuid().GetHashCode()), // Генерируем случайный int
            NewsId = dto.NewsId,
            Content = dto.Content,
            Created = DateTime.UtcNow
        };

        return await _repository.CreateAsync(post);
    }

    public async Task UpdateAsync(int id, PostDto dto)
    {
        await _repository.UpdateAsync(id, dto);
    }

    public async Task DeleteAsync(int id)
    {
        await _repository.DeleteAsync(id);
    }
}