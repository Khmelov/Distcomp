using DiscussionService.DTOs.Requests;
using DiscussionService.DTOs.Responses;
using DiscussionService.Models;

namespace DiscussionService.Interfaces
{
    public interface IPostService
    {
        Task<PostResponseTo> CreatePost(PostRequestTo post);
        Task<IEnumerable<PostResponseTo>> GetAllPosts();
        Task<PostResponseTo?> GetPost(PostRequestTo post);
        Task<PostResponseTo?> UpdatePost(PostRequestTo post);
        Task DeletePost(PostRequestTo post);
    }
}
