using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace API.Controllers
{
    [ApiController]
    [Route("api/v1.0/[controller]")]
    public class PostsController : ControllerBase
    {
        private readonly IPostService _postService;
        private readonly ILogger<PostsController> _logger;

        public PostsController(IPostService postService, ILogger<PostsController> logger)
        {
            _postService = postService;
            _logger = logger;
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
            var posts = await _postService.GetAllPosts();
            return Ok(posts);
        }

        [HttpGet("{id:long}")]
        public async Task<ActionResult<PostResponseTo>> GetPostById(long id)
        {
            var dto = new PostRequestTo { Id = id };
            var post = await _postService.GetPost(dto);
            if (post == null)
                return NotFound();
            return Ok(post);
        }

        [HttpPut("{id:long}")]
        public async Task<ActionResult<PostResponseTo>> UpdatePost(long id, [FromBody] PostRequestTo request)
        {
            request.Id = id;
            var updated = await _postService.UpdatePost(request);
            if (updated == null)
                return NotFound();
            return Ok(updated);
        }

        [HttpDelete("{id:long}")]
        public async Task<IActionResult> DeletePost(long id)
        {
            var dto = new PostRequestTo { Id = id };
            await _postService.DeletePost(dto);
            return NoContent();
        }
    }
}