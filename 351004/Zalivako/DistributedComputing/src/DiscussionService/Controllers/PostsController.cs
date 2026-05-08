using DiscussionService.DTOs.Responses;
using DiscussionService.DTOs.Requests;
using DiscussionService.Interfaces;
using DiscussionService.Models;
using Microsoft.AspNetCore.Mvc;

namespace DiscussionService.Controllers
{
    [ApiController]
    [Route("api/v1.0/[controller]")]
    public class PostsController : ControllerBase
    {
        private readonly IPostService _service;

        public PostsController(IPostService service)
        {
            _service = service;
        }

        [HttpPost]
        public async Task<ActionResult<PostResponseTo>> CreatePost([FromBody] PostRequestTo post)
        {
            var created = await _service.CreatePost(post);
            return CreatedAtAction(nameof(GetPostById), new { id = created.Id }, created);
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<PostResponseTo>>> GetAllPosts()
        {
            try
            {
                var posts = await _service.GetAllPosts();
                return Ok(posts);
            }
            catch(Exception)
            {
                return Ok();
            }
        }

        [HttpGet("{id:long}")]
        public async Task<ActionResult<PostResponseTo>> GetPostById(long id)
        {
            var dto = new PostRequestTo() { Id = id };
            var post = await _service.GetPost(dto);
            if (post == null)
                return NotFound();

            return Ok(post);
        }

        [HttpPut("{id:long}")]
        public async Task<ActionResult<PostResponseTo>> UpdatePost(long id, [FromBody] PostRequestTo post)
        {
            post.Id = id;
            var updated = await _service.UpdatePost(post);
            if (updated == null)
                return NotFound();

            return Ok(updated);
        }

        [HttpDelete("{id:long}")]
        public async Task<IActionResult> DeletePost(long id)
        {
            var dto = new PostRequestTo() { Id = id };
            await _service.DeletePost(dto);

            return NoContent();
        }
    }
}
