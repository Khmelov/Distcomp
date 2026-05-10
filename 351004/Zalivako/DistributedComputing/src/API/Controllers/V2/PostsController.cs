// PostsController.cs
using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace API.Controllers.V2
{
    [ApiController]
    [Route("api/v2.0/[controller]")]
    [Authorize(Roles = "ADMIN,CUSTOMER")]
    public class PostsController : ControllerBase
    {
        private readonly IPostService _postService;

        public PostsController(IPostService postService)
        {
            _postService = postService;
        }

        [HttpPost]
        public async Task<ActionResult<PostResponseTo>> CreatePost([FromBody] PostRequestTo request)
        {
            var created = await _postService.CreatePost(request);
            return CreatedAtAction(nameof(GetPostById), new { id = created.Id }, created);
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<PostResponseTo>>> GetAllPosts()
        {
            return Ok(await _postService.GetAllPosts());
        }

        [HttpGet("{id:long}")]
        public async Task<ActionResult<PostResponseTo>> GetPostById(long id)
        {
            var post = await _postService.GetPost(new PostRequestTo { Id = id });
            if (post == null) return NotFound();
            return Ok(post);
        }

        [HttpPut("{id:long}")]
        public async Task<ActionResult<PostResponseTo>> UpdatePost(long id, [FromBody] PostRequestTo request)
        {
            request.Id = id;
            var updated = await _postService.UpdatePost(request);
            if (updated == null) return NotFound();
            return Ok(updated);
        }

        [HttpDelete("{id:long}")]
        public async Task<IActionResult> DeletePost(long id)
        {
            await _postService.DeletePost(new PostRequestTo { Id = id });
            return NoContent();
        }
    }
}