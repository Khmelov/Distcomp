using Domain.Entities;

namespace Domain.Repository;

public interface IPostRepository
{
    Task<Post?> AddPost(Post post);
    Task<Post?> GetPost(long postId);
    Task<Post?> RemovePost(long postId);
    Task<Post?> UpdatePost(Post post);
    Task<IEnumerable<Post?>?> GetAllPosts();
    Task<IEnumerable<Post?>?> GetPostsByNewsId(long newsId);
}