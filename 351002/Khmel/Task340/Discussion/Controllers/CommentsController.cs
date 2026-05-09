using Microsoft.AspNetCore.Mvc;
using Discussion.DTOs;
using Discussion.Services;

namespace Discussion.Controllers
{
    [ApiController]
    [Route("api/v1.0/comments")]
    public class CommentsController : ControllerBase
    {
        private readonly ICommentService _service;

        public CommentsController(ICommentService service)
        {
            _service = service;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<CommentResponseTo>>> GetAll()
        {
            return Ok(await _service.GetAllAsync());
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<CommentResponseTo>> GetById(long id)
        {
            var result = await _service.GetByIdAsync(id);
            if (result == null)
                return NotFound();
            return Ok(result);
        }

        [HttpPost]
        public async Task<ActionResult<CommentResponseTo>> Create([FromBody] CommentRequestTo request)
        {
            var result = await _service.CreateAsync(request);
            return StatusCode(201, result);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<CommentResponseTo>> Update(long id, [FromBody] CommentRequestTo request)
        {
            var result = await _service.UpdateAsync(id, request);
            return Ok(result);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(long id)
        {
            await _service.DeleteAsync(id);
            return NoContent();
        }
    }
}