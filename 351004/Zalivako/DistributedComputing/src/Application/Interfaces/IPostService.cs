using Application.DTOs.Requests;
using Application.DTOs.Responses;

namespace Application.Interfaces
{
    public interface IPostService
    {
        Task<PostResponseTo> CreatePost(PostRequestTo createPostRequestTo);

        Task<IEnumerable<PostResponseTo>> GetAllPosts();

        Task<PostResponseTo> GetPost(PostRequestTo getPostRequestTo);

        Task<PostResponseTo> UpdatePost(PostRequestTo updatePostRequestTo);

        Task DeletePost(PostRequestTo deletePostRequestTo);
    }
}
