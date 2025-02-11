using Application.DTO.Request.Post;
using Application.DTO.Response.Post;

namespace Application.Interfaces;

public interface IPostService
{
    public Task<PostResponseToGetById?> CreatePost(PostRequestToCreate model);
    public Task<IEnumerable<PostResponseToGetById?>?> GetPosts(PostRequestToGetAll request);
    public Task<IEnumerable<PostResponseToGetById?>?> GetPostsByNewsId(PostRequestToGetByNewsId request);
    public Task<PostResponseToGetById?> GetPostById(PostRequestToGetById request);
    public Task<PostResponseToGetById?> UpdatePost(PostRequestToFullUpdate model);
    public Task<PostResponseToGetById?> DeletePost(PostRequestToDeleteById request);
}