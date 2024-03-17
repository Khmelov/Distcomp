using TaskREST.Dto.Request.CreateTo;
using TaskREST.Dto.Request.UpdateTo;
using TaskREST.Dto.Response;

namespace TaskREST.Services.Interfaces;

public interface IPostService
{
    Task<PostResponseTo> GetPostById(long id);
    Task<IEnumerable<PostResponseTo>> GetPosts();
    Task<PostResponseTo> CreatePost(CreatePostRequestTo createPostRequestTo);
    Task DeletePost(long id);
    Task<PostResponseTo> UpdatePost(UpdatePostRequestTo modifiedPost);
}