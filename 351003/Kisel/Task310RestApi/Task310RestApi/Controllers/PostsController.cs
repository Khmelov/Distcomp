
using Microsoft.AspNetCore.Mvc;
using Task310RestApi.DTOs.Request;
using Task310RestApi.DTOs.Response;
using Task310RestApi.Interfaces;

namespace Task310RestApi.Controllers
{
    [Route("api/v1.0/posts")]
    [ApiController]
    public class PostsController : ControllerBase
    {
        private readonly IPostService _postService;

        public PostsController(IPostService postService)
        {
            _postService = postService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<PostResponseTo>>> GetPosts()
        {
            var posts = await _postService.GetAllPostsAsync();
            return Ok(posts);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<PostResponseTo>> GetPost(long id)
        {
            try
            {
                var post = await _postService.GetPostByIdAsync(id);
                return Ok(post);
            }
            catch (Exceptions.ResourceNotFoundException)
            {
                return NotFound();
            }
        }

        [HttpPost]
        public async Task<ActionResult<PostResponseTo>> CreatePost([FromBody] PostRequestTo postRequest)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            try
            {
                var createdPost = await _postService.CreatePostAsync(postRequest);
                return CreatedAtAction(nameof(GetPost), new { id = createdPost.Id }, createdPost);
            }
            catch (Exceptions.ValidationException ex)
            {
                return BadRequest(new ErrorResponse(ex.Message, ex.ErrorCode));
            }
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<PostResponseTo>> UpdatePost(long id, [FromBody] PostRequestTo postRequest)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            try
            {
                var updatedPost = await _postService.UpdatePostAsync(id, postRequest);
                return Ok(updatedPost);
            }
            catch (Exceptions.ResourceNotFoundException)
            {
                return NotFound();
            }
            catch (Exceptions.ValidationException ex)
            {
                return BadRequest(new ErrorResponse(ex.Message, ex.ErrorCode));
            }
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeletePost(long id)
        {
            try
            {
                var result = await _postService.DeletePostAsync(id);
                if (result)
                {
                    return NoContent();
                }
                return NotFound();
            }
            catch (Exceptions.ResourceNotFoundException)
            {
                return NotFound();
            }
        }

        [HttpGet("by-news/{newsId}")]
        public async Task<ActionResult<IEnumerable<PostResponseTo>>> GetPostsByNewsId(long newsId)
        {
            try
            {
                var posts = await _postService.GetPostsByNewsIdAsync(newsId);
                return Ok(posts);
            }
            catch (Exceptions.ResourceNotFoundException)
            {
                return NotFound();
            }
        }
    }
}
