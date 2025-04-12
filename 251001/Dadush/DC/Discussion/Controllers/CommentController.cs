using Asp.Versioning;
using Discussion.Models;
using Discussion.Services;
using Discussion.Services.Implementation;
using Microsoft.AspNetCore.Mvc;

namespace Discussion.Controllers {

    [ApiController]
    [Route("api/v1.0/comments")]
    [ApiVersion("1.0")]
    public class CommentController : ControllerBase {

        private ICommentService commentService;
        private ILogger<CommentService> logger;

        public CommentController(ICommentService commentService, ILogger<CommentService> logger) {
            this.commentService = commentService;
            this.logger = logger;
        }

        [HttpGet]
        public async Task<IEnumerable<CommentOutDto>> GetAll() {
            return await commentService.GetAllAsync();
        }

        // GET */5
        [HttpGet("{id}")]
        public async Task<ActionResult> GetById(long id) {
            var Comment = await commentService.GetAsync(id);
            if (Comment == null) {
                return NotFound();
            } else {
                return Ok(Comment);
            }
        }

        // POST *
        [HttpPost]
        public async Task<ActionResult> Create([FromBody] CommentInDto newComment) {
            try {
                var Comment = await commentService.CreateAsync(newComment);
                return StatusCode(StatusCodes.Status201Created, Comment);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status403Forbidden);
            }
        }

        // PUT *
        [HttpPut]
        public async Task<ActionResult> Update([FromBody] Comment data) {
            try {
                var Comment = await commentService.UpdateAsync(data);
                return Ok(Comment);
            } catch (KeyNotFoundException ex) {
                return NotFound();
            }
        }

        // DELETE */5
        [HttpDelete("{id}")]
        public async Task<ActionResult> Delete(long id) {
            try {
                await commentService.DeleteAsync(id);
                return StatusCode(StatusCodes.Status204NoContent);
            } catch (KeyNotFoundException ex) {
                return NotFound();
            }
        }
    }
}
