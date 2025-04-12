using Asp.Versioning;
using DistComp.Models;
using DistComp.Services;
using DistComp.Services.Implementation;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace DistComp.Controllers {
    [Route("api/v1.0/comments")]
    [ApiController]
    [ApiVersion("1.0")]
    public class CommentController : ControllerBase {
        private readonly ICommentService commentService;
        private readonly ILogger<CommentService> logger;

        public CommentController(ICommentService service, ILogger<CommentService> logger) {
            this.commentService = service;
            this.logger = logger;
        }

        // GET *
        [HttpGet]
        public async Task<IEnumerable<CommentOutDto>> GetAll() {
            return await commentService.GetAll();
        }

        // GET */5
        [HttpGet("{id}")]
        public async Task<ActionResult> GetById(long id) {
            var Comment = await commentService.Get(id);
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
                var Comment = await commentService.Create(newComment);
                return StatusCode(StatusCodes.Status201Created, Comment);
            } catch (Exception ex) {
                return StatusCode(StatusCodes.Status403Forbidden);
            }
        }

        // PUT *
        [HttpPut]
        public async Task<ActionResult> Update([FromBody] Comment data) {
            try {
                var Comment = await commentService.Update(data);
                return Ok(Comment);
            } catch (KeyNotFoundException ex) {
                return NotFound();
            }
        }

        // DELETE */5
        [HttpDelete("{id}")]
        public async Task<ActionResult> Delete(long id) {
            try {
                await commentService.Delete(id);
                return StatusCode(StatusCodes.Status204NoContent);
            } catch (KeyNotFoundException ex) {
                return NotFound();
            }
        }
    }
}
