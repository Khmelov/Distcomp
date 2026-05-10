using Cassandra.Mapping;
using DiscussionService.Models.Dtos;
using DiscussionService.Models.Entities;

namespace DiscussionService.Repositories;

public class PostRepository : IPostRepository
{
    private readonly IMapper _mapper;

    public PostRepository(IMapper mapper)
    {
        _mapper = mapper;
    }

    public async Task<IEnumerable<Post>> GetAllAsync()
    {
        return await _mapper.FetchAsync<Post>("SELECT * FROM tbl_post");
    }

    public async Task<Post?> GetByIdAsync(int id)
    {
        return await _mapper.SingleOrDefaultAsync<Post>(
            "SELECT * FROM tbl_post WHERE id = ? ALLOW FILTERING",
            id);
    }

    public async Task<Post> CreateAsync(Post post)
    {
        await _mapper.InsertAsync(post);
        return post;
    }

    public async Task UpdateAsync(int id, PostDto dto)
    {
        await _mapper.UpdateAsync<Post>(
            "SET content = ? WHERE id = ?",
            dto.Content,
            id);
    }

    public async Task DeleteAsync(int id)
    {
        await _mapper.DeleteAsync<Post>(
            "WHERE id = ?",
            id);
    }
}