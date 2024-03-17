using Asp.Versioning;
using Microsoft.AspNetCore.Mvc;
using TaskREST.Dto.Request.CreateTo;
using TaskREST.Dto.Request.UpdateTo;
using TaskREST.Dto.Response;
using TaskREST.Models;
using TaskREST.Services.Interfaces;

namespace TaskREST.Controllers;

[Route("api/v{version:apiVersion}/posts")]
[ApiVersion("1.0")]
[ApiController]
public class PostController(IPostService service) : ControllerBase
{
    [HttpGet("{id:long}")]
    public async Task<ActionResult<Post>> GetPost(long id)
    {
        return Ok(await service.GetPostById(id));
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<Post>>> GetPosts()
    {
        return Ok(await service.GetPosts());
    }

    [HttpPost]
    public async Task<ActionResult<PostResponseTo>> CreatePost(CreatePostRequestTo createPostRequestTo)
    {
        var addedPost = await service.CreatePost(createPostRequestTo);
        return CreatedAtAction(nameof(GetPost), new { id = addedPost.Id }, addedPost);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> DeletePost(long id)
    {
        await service.DeletePost(id);
        return NoContent();
    }

    [HttpPut]
    public async Task<ActionResult<PostResponseTo>> UpdatePost(UpdatePostRequestTo updatePostRequestTo)
    {
        return Ok(await service.UpdatePost(updatePostRequestTo));
    }
}