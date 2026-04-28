using Microsoft.AspNetCore.Mvc;
using Publisher.Services;

namespace Publisher.Controllers
{
    [ApiController]
    [Route("api/v1.0/comments")]
    public class CommentsController : ControllerBase
    {
        private readonly IDiscussionClient _discussionClient;

        public CommentsController(IDiscussionClient discussionClient)
        {
            _discussionClient = discussionClient;
        }

        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            try
            {
                var result = await _discussionClient.GetAllCommentsAsync();
                return Ok(result);
            }
            catch (HttpRequestException ex)
            {
                return StatusCode(500, new { errorMessage = "Discussion service unavailable", errorCode = 50001 });
            }
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetById(long id)
        {
            try
            {
                var result = await _discussionClient.GetCommentByIdAsync(id);
                return Ok(result);
            }
            catch (HttpRequestException ex)
            {
                return StatusCode(500, new { errorMessage = "Discussion service unavailable", errorCode = 50001 });
            }
        }

        [HttpPost]
        public async Task<IActionResult> Create([FromBody] CommentRequestTo request)
        {
            try
            {
                var result = await _discussionClient.CreateCommentAsync(request);
                return StatusCode(201, result);
            }
            catch (HttpRequestException ex)
            {
                return StatusCode(500, new { errorMessage = "Discussion service unavailable", errorCode = 50001 });
            }
        }

        [HttpPut]
        public async Task<IActionResult> Update([FromBody] CommentRequestTo request)
        {
            try
            {
                var result = await _discussionClient.UpdateCommentAsync(request);
                return Ok(result);
            }
            catch (HttpRequestException ex)
            {
                return StatusCode(500, new { errorMessage = "Discussion service unavailable", errorCode = 50001 });
            }
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(long id)
        {
            try
            {
                await _discussionClient.DeleteCommentAsync(id);
                return NoContent();
            }
            catch (HttpRequestException ex)
            {
                return StatusCode(500, new { errorMessage = "Discussion service unavailable", errorCode = 50001 });
            }
        }
    }
}