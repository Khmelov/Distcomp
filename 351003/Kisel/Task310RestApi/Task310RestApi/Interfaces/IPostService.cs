using Task310RestApi.DTOs.Request;
using Task310RestApi.DTOs.Response;

namespace Task310RestApi.Interfaces
{
    public interface IPostService
    {
        Task<IEnumerable<PostResponseTo>> GetAllPostsAsync();
        Task<PostResponseTo?> GetPostByIdAsync(long id);
        Task<PostResponseTo> CreatePostAsync(PostRequestTo postRequest);
        Task<PostResponseTo?> UpdatePostAsync(long id, PostRequestTo postRequest);
        Task<bool> DeletePostAsync(long id);
        Task<bool> ExistsAsync(long id);
        Task<IEnumerable<PostResponseTo>> GetPostsByNewsIdAsync(long newsId);
    }
}